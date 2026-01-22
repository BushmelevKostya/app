package itmo.app.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Реализация AuditorAware для автоматического заполнения полей created_by и updated_by.
 * Использует Spring Security для получения email текущего аутентифицированного пользователя.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {
	
	@Override
	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
			return Optional.of("system");
		}
		
		// Получаем email из UserDetails (username в нашем случае - это email)
		return Optional.of(authentication.getName());
	}
}
