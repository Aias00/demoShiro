package com.aias.demo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoShiroApplication.class)
public class DemoShiroApplicationTests {

	private void login(String configFile, String userName, String password) {
		// 1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory(
				configFile);

		// 2、得到SecurityManager实例 并绑定给SecurityUtils
		org.apache.shiro.mgt.SecurityManager securityManager = factory
				.getInstance();
		SecurityUtils.setSecurityManager(securityManager);

		// 3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(userName,
				password);
		subject.login(token);
	}

	private Subject subject() {
		return SecurityUtils.getSubject();
	}

	@Test
	public void testHelloworld() {
		// 1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
		Factory<SecurityManager> factory = new IniSecurityManagerFactory(
				"classpath:shiro-jdbc-realm.ini");
		// 2、得到SecurityManager实例 并绑定给SecurityUtils
		org.apache.shiro.mgt.SecurityManager securityManager = factory
				.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		// 3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");

		try {
			// 4、登录，即身份验证
			subject.login(token);
		} catch (AuthenticationException e) {
			// 5、身份验证失败
		}

		Assert.assertEquals(true, subject.isAuthenticated()); // 断言用户已经登录

		// 6、退出
		subject.logout();
	}

	@Test
	public void roleTest() {
		login("classpath:shiro-role.ini", "zhang", "123");
		// 判断拥有角色：role1
		Assert.assertTrue(subject().hasRole("role1"));
		// 判断拥有角色：role1 and role2
		Assert.assertTrue(
				subject().hasAllRoles(Arrays.asList("role1", "role2")));
		// 判断拥有角色：role1 and role2 and !role3
		boolean[] result = subject()
				.hasRoles(Arrays.asList("role1", "role2", "role3"));
		Assert.assertEquals(true, result[0]);
		Assert.assertEquals(true, result[1]);
		Assert.assertEquals(false, result[2]);
	}

	@Test(expected = UnauthorizedException.class)
	public void testCheckRole() {
		login("classpath:shiro-role.ini", "zhang", "123");
		// 断言拥有角色：role1
		subject().checkRole("role1");
		// 断言拥有角色：role1 and role3 失败抛出异常
		subject().checkRoles("role1", "role3");
	}

	@Test
	public void testIsPermitted() {
		login("classpath:shiro-permission.ini", "zhang", "123");
		// 判断拥有权限：user:create
		Assert.assertTrue(subject().isPermitted("user:create"));
		// 判断拥有权限：user:update and user:delete
		Assert.assertTrue(
				subject().isPermittedAll("user:update", "user:delete"));
		// 判断没有权限：user:view
		Assert.assertFalse(subject().isPermitted("user:view"));
	}

	@Test(expected = UnauthorizedException.class)
	public void testCheckPermission() {
		login("classpath:shiro-permission.ini", "zhang", "123");
		// 断言拥有权限：user:create
		subject().checkPermission("user:create");
		// 断言拥有权限：user:delete and user:update
		subject().checkPermissions("user:delete", "user:update");
		// 断言拥有权限：user:view 失败抛出异常
		subject().checkPermissions("user:view");
	}
}
