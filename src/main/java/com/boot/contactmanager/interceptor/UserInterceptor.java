package com.boot.contactmanager.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;

// sets isAuthenticated flag on model
// after request has been processed
// helps thymeleaf render navbar etc accordingly
@Component
public class UserInterceptor implements HandlerInterceptor {
    public boolean isUserAuthenticated() {
        try {
            return !SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isRedirectView(ModelAndView mv) {
        String viewName = mv.getViewName();
        if (viewName.startsWith("redirect:/")) {
            return true;
        }
        View view = mv.getView();
        return (view != null && view instanceof SmartView && ((SmartView) view).isRedirectView());
    }

    private void addAuthStatus(ModelAndView model, boolean status) {
        model.addObject("isAuthenticated", status);
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object o, ModelAndView model)
            throws Exception {
        if (model != null && !isRedirectView(model)) {
            addAuthStatus(model, isUserAuthenticated());
        }
    }

}
