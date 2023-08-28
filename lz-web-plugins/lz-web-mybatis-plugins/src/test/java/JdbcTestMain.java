import cn.lz.web.mybatis.mapper.TestMapper;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Test;

/**
 * TODO
 *
 * @author 孔胜
 * @version 版权 Copyright(c)2023 LZJ
 * @date 2023/8/22 15:08
 */
public class JdbcTestMain {

    @Test
    public void test() {
        UnpooledDataSource dataSource = new UnpooledDataSource();
        dataSource.setDriver("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://127.0.0.1:5432/test?characterEncodeing=utf8&TimeZone=Asia/Shanghai&stringtype=unspecified");
        dataSource.setUsername("system");
        dataSource.setPassword("123456");
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(TestMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final TestMapper mapper = sqlSession.getMapper(TestMapper.class);
            final cn.lz.web.mybatis.model.Test test = mapper.selectBlog("1");
            System.out.println(test);
            System.out.println("");
        }
    }

    static class DefaultResultHandler implements ResultHandler<Object> {

        @Override
        public void handleResult(ResultContext<?> resultContext) {
            System.out.println("");
        }
    }
}
