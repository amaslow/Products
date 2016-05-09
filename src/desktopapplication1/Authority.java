package desktopapplication1;

import desktopapplication1.DesktopApplication1View.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class Authority {

    int col = 4;

    public static List<desktopapplication1.Items> QueryItem() {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String sql = "SELECT item,sap,descr_en,remarks_auth FROM elro.items WHERE remarks_auth is not null;";
        con = Utils.getConnection();
        List<Items> listAUTH = new ArrayList<Items>();

        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                Items model = new Items();

                model.setItem(rs.getString(1));
                model.setSap(rs.getString(2));
                model.setDescrEn(rs.getString(3));
                model.setRemarks_Auth(rs.getString(4));

                listAUTH.add(model);
            }
        } catch (SQLException e) {
        } finally {
            Utils.closeDB(rs, st, con);
        }
        return listAUTH;
    }

    public void CreateExcel() {
// Create a workbook 
        HSSFWorkbook workBook = new HSSFWorkbook();

// Create a worksheet named: 
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String d = String.valueOf(dateFormat.format(date));
        HSSFSheet sheet = workBook.createSheet("AuthorityRemarks_" + d);

            HSSFCellStyle styleTitleDescr = workBook.createCellStyle();
            styleTitleDescr.setBorderTop((short) 1); // 1:single lines border
            styleTitleDescr.setBorderLeft((short) 1);
            styleTitleDescr.setBorderRight((short) 1);
            styleTitleDescr.setBorderBottom((short) 1); // 6:double line border
            styleTitleDescr.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
            styleTitleDescr.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styleTitleDescr.setWrapText(true);
            styleTitleDescr.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont fontTitleDescr = workBook.createFont();
            fontTitleDescr.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            styleTitleDescr.setFont(fontTitleDescr);
            
            sheet.setColumnWidth(0, 12*256);
            sheet.setColumnWidth(2, 60*256);
            sheet.setColumnWidth(3, 100*256);
            
            

// Create a cell, starting from 2 
        HSSFCellStyle styleSubject = workBook.createCellStyle();
        HSSFFont fontSubject = workBook.createFont();
        fontSubject.setFontName("Arial");
        fontSubject.setFontHeightInPoints((short) 28);
        fontSubject.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        styleSubject.setFont(fontSubject);
        HSSFRow rowTitle = sheet.createRow((short) 1);
        rowTitle.setHeight((short) 650);
        HSSFCell cellTitle[] = new HSSFCell[1];
        cellTitle[0] = rowTitle.createCell(0);
        cellTitle[0].setCellStyle(styleSubject);
        cellTitle[0].setCellValue("Authority Remarks");

// Create a cell, starting from 4 
        HSSFRow row0 = sheet.createRow((short) 3);
        row0.setHeight((short) 650);
// Construct an array to set the cell after the line 
        HSSFCell cell[] = new HSSFCell[col];
        for (int i = 0; i < cell.length; i++) {
            cell[i] = row0.createCell(i);
            cell[i].setCellStyle(styleTitleDescr);
            //    sheet.autoSizeColumn((short) i);
        }
        cell[0].setCellValue("Item");
        cell[1].setCellValue("SAP");
        cell[2].setCellValue("Description");
        cell[3].setCellValue("Remarks");

// Get the data from the database query 
        List<Items> list = Authority.QueryItem();
        if (list != null && list.size() > 0) {
// Loop listERP data 
            for (int i = 0; i < list.size(); i++) {
                Items model = list.get(i);
                HSSFRow dataRow = sheet.createRow(i + 4);
                HSSFCell data[] = new HSSFCell[col];
                HSSFCellStyle styleData = workBook.createCellStyle();
                styleData.setBorderLeft((short) 1);
                styleData.setBorderRight((short) 1);
                for (int j = 0; j < data.length; j++) {
                    data[j] = dataRow.createCell(j);
                    //    sheet.autoSizeColumn((short) j);
                    data[j].setCellStyle(styleData);
                }

                data[0].setCellValue(model.getItem());
                data[1].setCellValue(model.getSap());
                data[2].setCellValue(model.getDescrEn());
                data[3].setCellValue(model.getRemarks_Auth());

                try {
// Output to XLS file 
                    File file = new File("G://QC//Authority Remarks//Authority Remarks " + d + ".xls");
                    FileOutputStream fos = new FileOutputStream(file);
// Write the data, and to close the file 
                    workBook.write(fos);
                    fos.close();

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }
            }
        }
        Desktop desktop = null;

        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }

        File folder = new File("G://QC//Authority Remarks//");
        try {
            desktop.open(folder);
        } catch (IOException ex) {
            Logger.getLogger(Authority.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
