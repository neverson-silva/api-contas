package com.dersaun.apicontas.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

public class Commons {
    /**
     * Create a URI
     * @param id
     * @return
     */
    public static URI createURI(Integer id) {
        return createURI(id, "/{id}");
    }

    /**
     * Create a uri
     * @param id
     * @param path
     * @return
     */
    public static URI createURI(Integer id, String path ) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path(path)
                .buildAndExpand(id)
                .toUri();
    }

    public static URI createFromUri(Integer id, String path, HttpServletRequest req) {
        return ServletUriComponentsBuilder.fromContextPath(req)
                .path(path)
                .buildAndExpand(id)
                .toUri();
    }
}
