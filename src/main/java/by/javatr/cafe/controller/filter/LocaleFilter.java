package by.javatr.cafe.controller.filter;

import by.javatr.cafe.constant.SessionAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Set locale
 */

public class LocaleFilter implements Filter {

    public static final String SERVLET = "/controller";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();


        if(session.getAttribute(SessionAttributes.LOCALE) == null) {
            session.setAttribute(SessionAttributes.LOCALE, "en");
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getParameter("command_locale") != null){
            session.setAttribute(SessionAttributes.LOCALE, request.getParameter("command_locale"));
            response.sendRedirect(request.getRequestURI());
            return;
        }

        if(session.getAttribute(SessionAttributes.LOCALE) != null ){
            filterChain.doFilter(request,response);
            return;
        }

        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
