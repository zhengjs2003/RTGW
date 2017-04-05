/*
 * Copyright 2013 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package filters.pre

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.Debug
import com.netflix.zuul.context.RequestContext

import javax.servlet.http.HttpServletRequest


/**
 * @author mhawthorne
 */
class PreDecorationFilter extends ZuulFilter {

    @Override
    int filterOrder() {
        return 5
    }

    @Override
    String filterType() {
        return "pre"
    }

    @Override
    boolean shouldFilter() {
        return false;
    }

    @Override
    Object run() {
        RequestContext ctx = RequestContext.getCurrentContext()
        // sets origin
//        ctx.setRouteHost(new URL("http://httpbin.org"));

        HttpServletRequest req = RequestContext.currentContext.request as HttpServletRequest
        //Debug.addRequestDebug("REQUEST:: " + req.getScheme() + " " + req.getRemoteAddr() + ":" + req.getRemotePort())

        //Debug.addRequestDebug("REQUEST:: > " + req.getMethod() + " " + req.getRequestURI() + " " + req.getProtocol())

        String uri = req.getRequestURI();

        Debug.addRequestDebug("zz uri="+uri);
        if(uri == null){
            ctx.setRouteHost(new URL("http://www.sina.com"));
        }

        if(uri.startsWith("/tg")) {
            ctx.setRouteHost(new URL("http://11.8.56.201:8080"));
        }else if(uri.startsWith("/iap")){
            ctx.setRouteHost(new URL("http://11.8.37.60:8080/"));
        }else{
            // sets origin
            ctx.setRouteHost(new URL("http://www.sina.com"));
        }

        // sets custom header to send to the origin
        ctx.addOriginResponseHeader("cache-control", "max-age=3600");
        ctx.addZuulRequestHeader("ztoken", "z=aa");
    }

}
