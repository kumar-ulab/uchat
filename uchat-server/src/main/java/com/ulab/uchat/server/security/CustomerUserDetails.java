//package com.ulab.uchat.server.security;
//
//import java.util.Collection;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//public class CustomerUserDetails implements UserDetails {
//
//	private static final long serialVersionUID = 1L;
//	private AccountsEntity account = null;
//	// 存放权限的集合
//	private Collection<? extends GrantedAuthority> authorities = null;
//	private boolean accountNonExpired = false;
//	private boolean accountNonLocked = false;
//	private boolean credentialsNonExpired = false;
//	private boolean enabled = false;
//
//	public CustomerUserDetails(AccountsEntity account, Collection<? extends GrantedAuthority> authorities) {
//		this(account, true, true, true, true, authorities);
//	}
//
//	public CustomerUserDetails(AccountsEntity account, boolean enabled, boolean accountNonExpired,
//                               boolean credentialsNonExpired, boolean accountNonLocked,
//                               Collection<? extends GrantedAuthority> authorities) {
//		if (account.getUsername() != null && !"".equals(account.getUsername()) && account.getPassword() != null) {
//			this.account = account;
//			this.enabled = enabled;
//			this.accountNonExpired = accountNonExpired;
//			this.credentialsNonExpired = credentialsNonExpired;
//			this.accountNonLocked = accountNonLocked;
//			this.authorities = authorities;
//		} else {
//			throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
//		}
//	}
//
//	public AccountsEntity getAccountsEntity() {
//		return account;
//	}
//
//	public void setAccountsEntity(AccountsEntity account) {
//		this.account = account;
//	}
//
//	public boolean equals(Object rhs) {
//		return rhs instanceof CustomerUserDetails
//				&& this.getUsername().equals(((CustomerUserDetails) rhs).getUsername());
//	}
//
//	public int hashCode() {
//		return this.getUsername().hashCode();
//	}
//
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return this.authorities;
//	}
//
//	@Override
//	public String getPassword() {
//		return this.account.getPassword();
//	}
//
//	@Override
//	public String getUsername() {
//		return this.account.getUsername();
//	}
//
//	@Override
//	public boolean isAccountNonExpired() {
//		return this.accountNonExpired;
//	}
//
//	@Override
//	public boolean isAccountNonLocked() {
//		return this.accountNonLocked;
//	}
//
//	@Override
//	public boolean isCredentialsNonExpired() {
//		return this.credentialsNonExpired;
//	}
//
//	@Override
//	public boolean isEnabled() {
//		return this.enabled;
//	}
//}
