package com.marketing.web.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    private final List<Locale> localeList = Arrays.asList(new Locale("tr"), new Locale("en"));

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
       String lang = request.getHeader("Accept-Language");
       return lang == null ? Locale.getDefault() : Locale.lookup(Locale.LanguageRange.parse(lang), localeList);
    }
}
