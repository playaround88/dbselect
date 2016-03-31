import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.ai.dbselect.common.SqlTypeUtil;


public class SyntaxTest {

	@Test
	public void test() {
//		String sql="select * from t_user";
		String sql="update t_user set 1=1 where id=1";
//		String sql="delete from t_user where 1=1";
//		String sql="insert\r\nt_user values()";
		
		System.out.println(SqlTypeUtil.type(sql));
	}
	
	@Test
	public void regexTest(){
		String alg="wrr";
//		String alg="rr";
		Assert.assertTrue(Pattern.matches(".*w.*", alg));
	}

}
