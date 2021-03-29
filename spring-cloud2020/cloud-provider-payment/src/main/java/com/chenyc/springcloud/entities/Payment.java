package com.chenyc.springcloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author chenyc
 * @create 2021-03-29 16:07
 */
@Data
public class Payment implements Serializable {
    private  long id;
    private String serial;

    public Payment(){

    }
}
