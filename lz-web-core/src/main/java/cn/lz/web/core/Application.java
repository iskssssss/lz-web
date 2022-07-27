package cn.lz.web.core;

import cn.hutool.core.io.FileUtil;
import cn.lz.beans.anno.Bean;
import cn.lz.beans.factory.BeanFactory;
import cn.lz.beans.scanner.Scanner;
import cn.lz.web.core.anno.router.Router;
import cn.lz.web.core.environment.Environment;
import cn.lz.web.core.factory.WebBeanFactory;
import cn.lz.web.core.hanlder.HttpServerInitializer;
import cn.lz.web.core.router.RouterCore;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.FastThreadLocalThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2022 LZJ
 * @date 2022/7/11 11:44
 */
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * 端口
     */
    private int port;

    /**
     * 运行状态
     */
    private final AtomicBoolean startStatus = new AtomicBoolean(false);

    private final EventLoopGroup boosGroup;
    private final EventLoopGroup workerGroup;

    private final Class<?> primarySource;

    /**
     * bean工厂
     */
    private final BeanFactory beanFactory;

    /**
     * 配置文件
     */
    private final Environment environment;

    private final RouterCore routerCore;

    public Application(Class<?> primarySource) throws Exception {
        this.primarySource = primarySource;

        this.environment = new Environment();
        this.environment.init(this.primarySource);

        this.beanFactory = new WebBeanFactory(this);

        this.routerCore = new RouterCore();

        this.boosGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(8);
    }

    public void start(String[] args) throws Exception {
        this.start(environment.getValInt("server.port", 6969), args);
    }

    public void start(int port, String[] args) throws Exception {
        this.port = port;
        Package aPackage = this.primarySource.getPackage();
        this.scanBean(aPackage.getName());
        this.initRouter();
        new FastThreadLocalThread(this::_startServer).start();
        while (startStatus.get() == false) {}
        logger.info("start success - port:" + this.port);
        URL resource = this.primarySource.getResource("/banner.txt");
        if (resource == null) {
            return;
        }
        System.out.println(FileUtil.readString(resource, StandardCharsets.UTF_8));
    }

    /**
     * 扫描Bean
     *
     * @param packageName 扫描路径
     * @throws Exception 异常
     */
    private void scanBean(String packageName) throws Exception {
        Set<Class<?>> classes = Scanner.getAnnotationClasses(packageName, Bean.class);
        for (Class<?> aClass : classes) {
            this.beanFactory.createBean(aClass, aClass.getSimpleName());
        }
    }

    /**
     * 初始化路由信息
     */
    private void initRouter() {
        Map<String, Object> routerMap = this.beanFactory.getBeanByAnnotation(Router.class);
        routerCore.initRouter(routerMap);
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public RouterCore getRouterCore() {
        return routerCore;
    }

    private void _startServer() {
        try {
            // 启动异常监听服务
            Thread shutdownThread = new Thread(this::stop);
            shutdownThread.setName("lz.web-shutdown@thread");
            Runtime.getRuntime().addShutdownHook(shutdownThread);
            // 启动服务
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(this.boosGroup, this.workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer(this));
            ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();
            startStatus.set(true);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (!startStatus.get()) {
            return;
        }
        startStatus.set(false);
        logger.info("正在关闭中...");
        this.boosGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    /**
     * 运行web
     *
     * @param primarySource 源
     * @param args          参数
     * @throws Exception 异常
     */
    public static Application start(Class<?> primarySource, String[] args) throws Exception {
        Application application = new Application(primarySource);
        application.start(args);
        return application;
    }
}
