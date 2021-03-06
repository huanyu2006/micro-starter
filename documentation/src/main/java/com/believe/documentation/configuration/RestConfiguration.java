package com.believe.documentation.configuration;

import com.believe.query.users.domain.Users;
import org.springframework.boot.autoconfigure.web.WebMvcRegistrationsAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * <p> Data rest configuration. </p>
 *
 * @author Li Xingping
 */
@Configuration
public class RestConfiguration extends RepositoryRestMvcConfiguration {

  @Bean
  public WebMvcRegistrationsAdapter webMvcRegistrationsHandlerMapping() {
    return new WebMvcRegistrationsAdapter() {
      @Override
      public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping() {
          private final static String API_BASE_PATH = "api";

          @Override
          protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
            Class<?> beanType = method.getDeclaringClass();
            if (AnnotationUtils.findAnnotation(beanType, RestController.class) != null) {
              PatternsRequestCondition apiPattern = new PatternsRequestCondition(API_BASE_PATH)
                .combine(mapping.getPatternsCondition());

              mapping = new RequestMappingInfo(mapping.getName(), apiPattern,
                mapping.getMethodsCondition(), mapping.getParamsCondition(),
                mapping.getHeadersCondition(), mapping.getConsumesCondition(),
                mapping.getProducesCondition(), mapping.getCustomCondition());
            }

            super.registerHandlerMethod(handler, method, mapping);
          }
        };
      }
    };
  }

  @Configuration
  static class RestConfigurationExposeId extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
      config.exposeIdsFor(Users.class);
      config.setBasePath("/api");
    }
  }

}