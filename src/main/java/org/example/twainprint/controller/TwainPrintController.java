package org.example.twainprint.controller;


import org.example.twainprint.entity.TwainMachineName;
import org.example.twainprint.jtwain.exceptions.TwainException;
import org.example.twainprint.jtwain.scan.Source;
import org.example.twainprint.jtwain.scan.SourceManager;
import org.example.twainprint.jtwain.transfer.TwainFileTransfer;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
public class TwainPrintController {

    /**
     * 查询扫描仪twain驱动
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryTwainMachineNameList", method = RequestMethod.GET)
    public Object queryTwainMachineNameList(HttpServletRequest req)  {
        try {
            return SourceManager.instance().getSources();
        }catch (TwainException e){
            return e.getMessage();
        }
    }

    public static String fileToBase64(String filePath) {
        String base64String = "";
        File file = new File(filePath);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);
            base64String = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64String;
    }

//
//开始扫描
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/scan", method = RequestMethod.GET)
    public Object sacn(TwainMachineName twainMachineName, HttpServletRequest req)  {
        try {
            if (twainMachineName.getNameIndex() != null){
              Source source =  SourceManager.instance().getSources().get(twainMachineName.getNameIndex());
              //设置是否调用系统UI
              source.setSystemUI(twainMachineName.getSystemUI());
              //设置是否高扫的自动进纸
              source.setAutoDocumentFeeder(true);
              //设置扫描颜色
              if (twainMachineName.getColor().equals("0")){
                  source.setColor(Source.ColorMode.COLOR);
              }else if (twainMachineName.getColor().equals("1")){
                  source.setColor(Source.ColorMode.BW);
              }else if (twainMachineName.getColor().equals("2")){
                  source.setColor(Source.ColorMode.GRAYSCALE);
              }
              //设置扫描dpi
              source.setDpi(twainMachineName.getDpi());
              //设置是否双面扫描
              source.setDoubleSide(twainMachineName.getDoubleSide());
              //是否去除白页
             // source.setRemoveBlankSide(twainMachineName.getRemoveBlankSide());
              //是否开启自动校正
              source.setMaticdskem(twainMachineName.getMaticdskem());
              //图片格式
              source.setXhr(twainMachineName.getXhr());
              //是否开启自动边缘检测
             // source.setMaticborderdetection(twainMachineName.getMaticborderdetection());
                TwainFileTransfer.filelist = new ArrayList<>();
                TwainFileTransfer.filetype = null;
              //开始扫描
              source.scan();
              FileData fileData = new FileData();
                for (String e:TwainFileTransfer.filelist){
                    if(TwainFileTransfer.filetype.equals("pdf")) {
                        String a = "data:application/pdf;base64," + fileToBase64(e);
                        fileData.getFile().add(a);
                        fileData.setType("pdf");
                    }else {
                        String b = "data:application/jpg;base64," + fileToBase64(e);
                        fileData.getFile().add(b);
                        fileData.setType("jpg");
                    }
                };

              //SourceManager.instance().freeResources();
              return fileData;
            }else {
                SourceManager.instance().freeResources();
                return "未选择twain";
            }

        }catch (TwainException e){
            return e.getMessage();
        }
    }

//http://localhost:8080/scan?nameIndex=2&systemUI=false&color=0&dpi=300&doubleSide=true&maticdskem=true&xhr=4
}
