package user.security

import com.andreformento.money.user.security.UserAuthPassword
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(private val userAuthPassword: UserAuthPassword) : UserDetails {
    override fun getAuthorities() = mutableListOf<GrantedAuthority>()

    override fun isEnabled() = true

    override fun getUsername() = userAuthPassword.user.name

    override fun isCredentialsNonExpired() = true

    override fun getPassword() = userAuthPassword.password

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true
}
