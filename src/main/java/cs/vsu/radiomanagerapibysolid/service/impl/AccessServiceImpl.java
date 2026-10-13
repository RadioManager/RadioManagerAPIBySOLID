package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.service.inter.AccessService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccessServiceImpl implements AccessService {

    @Override
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf((String) auth.getPrincipal());
    }

    @Override
    public boolean isOwnerOrAdmin(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin || getCurrentUserId().equals(userId);
    }

}
