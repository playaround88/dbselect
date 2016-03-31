package dbselectTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ai.dbselectTest.User;
import com.ai.dbselectTest.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:applicationContext.xml"})
public class DbSelectTest extends AbstractJUnit4SpringContextTests {
	private UserService service;
	@Autowired
	public void setService(UserService service) {
		this.service = service;
	}
	
	private int loop=10;
	private User user;
	private void genUser(){
		user=new User();
		user.setName("wutianbiao");
	}
	@Test
	public void test() {
		genUser();
		service.insert(user);
		user.setName("lixu");
		service.update(user);
		User u=null;
		for(int i=0; i<loop; i++){
			u=service.selectById(user.getId());
			if(u!=null){
				System.out.println(u.toString());
			}else{
				System.out.println("没有查询到数据!");
			}
		}
		service.del(user.getId());
	}
}