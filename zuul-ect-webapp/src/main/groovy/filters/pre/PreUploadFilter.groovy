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
import com.netflix.zuul.context.RequestContext
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.FileUploadException
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload

import javax.servlet.http.HttpServletRequest
/**
 * @author mhawthorne
 */
class PreUploadFilter extends ZuulFilter {

    @Override
    int filterOrder() {
        return 3
    }

    @Override
    String filterType() {
        return "pre"
    }

    @Override
    boolean shouldFilter() {
        HttpServletRequest request = RequestContext.currentContext.request as HttpServletRequest
        return request.getHeader("Content-type").contains("multipart/form-data");
    }

    @Override
    Object run() {
        RequestContext ctx = RequestContext.getCurrentContext()
        HttpServletRequest request = RequestContext.currentContext.request as HttpServletRequest

        //Debug.addRequestDebug("REQUEST:: " + req.getScheme() + " " + req.getRemoteAddr() + ":" + req.getRemotePort())
        //Debug.addRequestDebug("REQUEST:: > " + req.getMethod() + " " + req.getRequestURI() + " " + req.getProtocol())

        String uri = request.getRequestURI();

        request.setCharacterEncoding("utf-8");  //设置编码

        //获得磁盘文件条目工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //获取文件需要上传到的路径
        String path = request.getRealPath("/upload");

        //如果没以下两行设置的话，上传大的 文件 会占用 很多内存，
        //设置暂时存放的 存储室 , 这个存储室，可以和 最终存储文件 的目录不同
        /**
         * 原理 它是先存到 暂时存储室，然后在真正写到 对应目录的硬盘上，
         * 按理来说 当上传一个文件时，其实是上传了两份，第一个是以 .tem 格式的
         * 然后再将其真正写到 对应目录的硬盘上
         */
        factory.setRepository(new File(path));
        //设置 缓存的大小，当上传文件的容量超过该缓存时，直接放到 暂时存储室
        factory.setSizeThreshold(1024 * 1024);

        //高水平的API文件上传处理
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            //可以上传多个文件
            List<FileItem> list = (List<FileItem>) upload.parseRequest(request);

            for (FileItem item : list) {
                //获取表单的属性名字
                String name = item.getFieldName();

                //如果获取的 表单信息是普通的 文本 信息
                if (item.isFormField()) {
                    //获取用户具体输入的字符串 ，名字起得挺好，因为表单提交过来的是 字符串类型的
                    String value = item.getString();
//                    request.setAttribute(name, value);
                    System.out.println(name+"  "+value)
                }
                //对传入的非 简单的字符串进行处理 ，比如说二进制的 图片，电影这些
                else {
                    System.out.println(name+"  "+item.getSize())
                    InputStream inputStream = item.getInputStream();

                    int length = 0 ;
                    byte [] buf = new byte[1024] ;

                    System.out.println("获取上传文件的总共的容量："+item.getSize());

                    while((length = inputStream.read(buf)) != -1)
                    {
                        System.out.println( new String(buf, 0, length) )
                    }
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
