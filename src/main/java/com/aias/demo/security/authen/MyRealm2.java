package com.aias.demo.security.authen;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;

public class MyRealm2 implements Realm {
	@Override
	public String getName() {
		return "MyRealm";
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		// 仅支持UsernamePasswordToken类型的Token
		return token instanceof UsernamePasswordToken;
	}

	@Override
	public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token)
			throws AuthenticationException {
		System.out.println("第二层校验");
		// 得到用户名
		String username = (String) token.getPrincipal();
		// 得到密码
		String password = new String((char[]) token.getCredentials());
		// 自定义校验用户名和密码
		if (!"zhang".equals(username)) {
			// 用户名错误
			throw new UnknownAccountException();
		}
		if (!"123".equals(password)) {
			// 密码错误
			throw new IncorrectCredentialsException();
		}
		// 如果身份认证验证成功，返回一个AuthenticationInfo实现；
		return new SimpleAuthenticationInfo(username, password, getName());
	}
}