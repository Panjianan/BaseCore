package com.tsubasa.core.template;

import java.util.Map;

/**
 * Created by tsubasa on 2017/11/14.
 */
public interface IRouteRoot {
    @SuppressWarnings("unused")
    void loadInto(Map<Class, String> routes);
}
