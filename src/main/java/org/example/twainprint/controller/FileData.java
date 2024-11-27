package org.example.twainprint.controller;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：pings
 * @date ：Created in 2024-9-13 20:18
 * @description：
 * @modified By：
 * @version: $
 */
@Data
public class FileData {
    List<String> file = new ArrayList<>();
    String type = "";

}
