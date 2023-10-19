package nl.jnext.workshop.testcontainers.vakantieplanner.controller.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Credits to https://stackoverflow.com/a/60012403
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@AuthenticationPrincipal(expression = "@fetchUser.apply(#this)", errorOnInvalidType=true)
public @interface CurrentUser {
}
