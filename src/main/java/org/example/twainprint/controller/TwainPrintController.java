package org.example.twainprint.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.twainprint.entity.TwainMachineName;
import org.example.twainprint.jtwain.exceptions.TwainException;
import org.example.twainprint.jtwain.scan.Source;
import org.example.twainprint.jtwain.scan.SourceManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/san", method = RequestMethod.GET)
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
              source.setRemoveBlankSide(twainMachineName.getRemoveBlankSide());
              //是否开启自动校正
              source.setMaticdskem(twainMachineName.getMaticdskem());
              //是否开启自动边缘检测
              source.setMaticborderdetection(twainMachineName.getMaticborderdetection());
              //开始扫描
              source.scan();
              SourceManager.instance().freeResources();
              return "ok";
            }else {
                return "未选择twain";
            }

        }catch (TwainException e){
            return e.getMessage();
        }
    }


}
