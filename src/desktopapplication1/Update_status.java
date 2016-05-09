package desktopapplication1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.record.cf.PatternFormatting;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class Update_status {

    void CreateExcel() throws IOException, ParseException {
        String excelname = "G:\\QC\\CERTIFICATION OVERVIEW 2015.xlsx";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String sap, item, qc_status, status, hierarchy, descr_en, brand, vendor, supplier, item_s;
        String lvd_ce, lvd_cert, lvd_tr, oem_ce, gs_ce, gs_tr, photobiol_tr, ipclass_tr, emc_ce, emc_cert, emc_tr, rf_ce, rf_cert, rf_tr;
        String eup_ce, eup_tr, flux_tr, rohs_tr, reach_ce, pah_ce, cpd_dir, cpd_ce, cpd_tr;
        String vds_ce, vds_tr, nf_ce, nf_tr, bosec_ce, komo_ce, kk_ce, batt_m, batt_tr2, remarks, remarks_auth, return_place, mod_date, mod_who;
        int gs_cdf, doi, doc;
        int rownr = 3;
        FileInputStream fis = null;
        fis = new FileInputStream(new File(excelname));
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);
        wb.setSheetName(0, String.valueOf(dateFormat.format(date)));
        int last = (sheet.getLastRowNum() + 1);

        if (last > 2) {
            sheet.shiftRows(4, last, -1);

        }
        try {
            con = Utils.getConnection();
            st = con.createStatement();

            String SQL_GO = "SELECT sap,item,qm_status,status,hierarchy,descr_en,brand,vendor,supplier,item_s,"
                    + "lvd_ce,lvd_cert,lvd_tr,oem_ce,gs_ce,gs_tr,gs_cdf,photobiol_tr,ipclass_tr,emc_ce,emc_cert,emc_tr,rf_ce,rf_cert,rf_tr,"
                    + "cpd_dir,cpd_ce,cpd_tr,eup_ce,eup_tr,flux_tr,rohs_tr,reach_ce,pah_ce,"
                    + "vds_ce,vds_tr,nf_ce,nf_tr,bosec_ce,komo_ce,kk_ce,batt_m, batt_tr2,doc,doi,remarks,remarks_auth,return_place,mod_date,mod_who "
                    + "FROM elro.items where status!='N/A' ORDER BY sap;";
            rs = st.executeQuery(SQL_GO);

            CellStyle style_border = wb.createCellStyle();
            style_border.setBorderBottom((short) 1);
            style_border.setBorderTop((short) 1);
            style_border.setBorderLeft((short) 1);
            style_border.setBorderRight((short) 1);

            CellStyle style_red = wb.createCellStyle();
            style_red.setBorderBottom((short) 1);
            style_red.setBorderTop((short) 1);
            style_red.setBorderLeft((short) 1);
            style_red.setBorderRight((short) 1);
            style_red.setFillForegroundColor(IndexedColors.RED.getIndex());
            style_red.setFillPattern(CellStyle.SOLID_FOREGROUND);

            CellStyle style_green = wb.createCellStyle();
            style_green.setBorderBottom((short) 1);
            style_green.setBorderTop((short) 1);
            style_green.setBorderLeft((short) 1);
            style_green.setBorderRight((short) 1);
            style_green.setFillForegroundColor((short) 57);
            style_green.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

            CellStyle style_orange = wb.createCellStyle();
            style_orange.setBorderBottom((short) 1);
            style_orange.setBorderTop((short) 1);
            style_orange.setBorderLeft((short) 1);
            style_orange.setBorderRight((short) 1);
            style_orange.setFillForegroundColor((short) 51);
            style_orange.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

            while (rs.next()) {
                XSSFRow row = sheet.createRow(rownr);

                sap = rs.getString(1);
                XSSFCell sapCell = row.createCell(0);
                sapCell.setCellValue(sap);
                sapCell.setCellStyle(style_border);

                item = rs.getString(2);
                XSSFCell itemCell = row.createCell(1);
                itemCell.setCellValue(item);
                itemCell.setCellStyle(style_border);

                qc_status = rs.getString(3);
                XSSFCell qc_statusCell = row.createCell(2);
                qc_statusCell.setCellValue(qc_status);
                if (qc_status.equals("RED")) {
                    qc_statusCell.setCellStyle(style_red);
                } else if (qc_status.equals("GREEN")) {
                    qc_statusCell.setCellStyle(style_green);
                } else {
                    qc_statusCell.setCellStyle(style_orange);
                }

                status = rs.getString(4);
                XSSFCell statusCell = row.createCell(3);
                statusCell.setCellValue(status);
                statusCell.setCellStyle(style_border);

                hierarchy = rs.getString(5);
                XSSFCell hierarchyCell = row.createCell(4);
                hierarchyCell.setCellValue(hierarchy);
                hierarchyCell.setCellStyle(style_border);

                descr_en = rs.getString(6);
                XSSFCell descr_enCell = row.createCell(5);
                descr_enCell.setCellValue(descr_en);
                descr_enCell.setCellStyle(style_border);

                brand = rs.getString(7);
                XSSFCell brandCell = row.createCell(6);
                brandCell.setCellValue(brand);
                brandCell.setCellStyle(style_border);

                supplier = rs.getString(8);
                XSSFCell supplierCell = row.createCell(7);
                supplierCell.setCellValue(supplier);
                supplierCell.setCellStyle(style_border);

                vendor = rs.getString(9);
                XSSFCell vendorCell = row.createCell(8);
                vendorCell.setCellValue(vendor);
                vendorCell.setCellStyle(style_border);

                item_s = rs.getString(10);
                XSSFCell item_sCell = row.createCell(9);
                item_sCell.setCellValue(item_s);
                item_sCell.setCellStyle(style_border);

                lvd_ce = rs.getString(11);
                XSSFCell lvd_ceCell = row.createCell(10);
                if (lvd_ce != null) {
                    lvd_ceCell.setCellValue(lvd_ce);
                    lvd_ceCell.setCellStyle(style_green);
                } else {
                    lvd_ceCell.setCellValue("NA");
                    lvd_ceCell.setCellStyle(style_border);
                }

                lvd_cert = rs.getString(12);
                XSSFCell lvd_certCell = row.createCell(11);
                if (lvd_cert != null && lvd_cert.contains("MISSING")) {
                    lvd_certCell.setCellValue("MISSING");
                    lvd_certCell.setCellStyle(style_red);
                } else if (lvd_cert != null && !lvd_cert.equals("") && !lvd_cert.equals("NA")) {
                    lvd_certCell.setCellValue(lvd_cert);
                    lvd_certCell.setCellStyle(style_green);
                } else {
                    lvd_certCell.setCellValue("NA");
                    lvd_certCell.setCellStyle(style_border);
                }

                lvd_tr = rs.getString(13);
                XSSFCell lvd_trCell = row.createCell(12);
                if (lvd_tr != null && lvd_tr.contains("MISSING")) {
                    lvd_trCell.setCellValue("MISSING");
                    lvd_trCell.setCellStyle(style_red);
                } else if (lvd_tr != null && !lvd_tr.equals("") && !lvd_tr.equals("NA")) {
                    lvd_trCell.setCellValue(lvd_tr);
                    lvd_trCell.setCellStyle(style_green);
                } else {
                    lvd_trCell.setCellValue("NA");
                    lvd_trCell.setCellStyle(style_border);
                }

                oem_ce = rs.getString(14);
                XSSFCell oem_ceCell = row.createCell(13);
                if (oem_ce != null) {
                    oem_ceCell.setCellValue(oem_ce);
                    oem_ceCell.setCellStyle(style_green);
                } else {
                    oem_ceCell.setCellValue("NA");
                    oem_ceCell.setCellStyle(style_border);
                }

                gs_ce = rs.getString(15);
                XSSFCell gs_ceCell = row.createCell(14);
                if (gs_ce != null) {
                    gs_ceCell.setCellValue(gs_ce);
                    gs_ceCell.setCellStyle(style_green);
                } else {
                    gs_ceCell.setCellValue("NA");
                    gs_ceCell.setCellStyle(style_border);
                }

                gs_tr = rs.getString(16);
                XSSFCell gs_trCell = row.createCell(15);
                if (gs_tr != null) {
                    gs_trCell.setCellValue(gs_tr);
                    gs_trCell.setCellStyle(style_green);
                } else {
                    gs_trCell.setCellValue("NA");
                    gs_trCell.setCellStyle(style_border);
                }

                gs_cdf = rs.getInt(17);
                XSSFCell gs_cdfCell = row.createCell(16);
                if (gs_cdf == 0) {
                    gs_cdfCell.setCellValue("NA");
                    gs_cdfCell.setCellStyle(style_border);
                } else {
                    gs_cdfCell.setCellValue("YES");
                    gs_cdfCell.setCellStyle(style_green);
                }

                photobiol_tr = rs.getString(18);
                XSSFCell photobiol_trCell = row.createCell(17);
                if (photobiol_tr != null && photobiol_tr.contains("MISSING")) {
                    photobiol_trCell.setCellValue("MISSING");
                    photobiol_trCell.setCellStyle(style_red);
                } else if (photobiol_tr != null) {
                    photobiol_trCell.setCellValue(photobiol_tr);
                    photobiol_trCell.setCellStyle(style_green);
                } else {
                    photobiol_trCell.setCellValue("NA");
                    photobiol_trCell.setCellStyle(style_border);
                }

                ipclass_tr = rs.getString(19);
                XSSFCell ipclass_trCell = row.createCell(18);
                if (ipclass_tr != null && ipclass_tr.contains("MISSING")) {
                    ipclass_trCell.setCellValue("MISSING");
                    ipclass_trCell.setCellStyle(style_red);
                } else if (ipclass_tr != null) {
                    ipclass_trCell.setCellValue(ipclass_tr);
                    ipclass_trCell.setCellStyle(style_green);
                } else {
                    ipclass_trCell.setCellValue("NA");
                    ipclass_trCell.setCellStyle(style_border);
                }

                emc_ce = rs.getString(20);
                XSSFCell emc_ceCell = row.createCell(19);
                if (emc_ce != null) {
                    emc_ceCell.setCellValue(emc_ce);
                    emc_ceCell.setCellStyle(style_green);
                } else {
                    emc_ceCell.setCellValue("NA");
                    emc_ceCell.setCellStyle(style_border);
                }

                emc_cert = rs.getString(21);
                XSSFCell emc_certCell = row.createCell(20);
                if (emc_cert != null && emc_cert.contains("MISSING")) {
                    emc_certCell.setCellValue("MISSING");
                    emc_certCell.setCellStyle(style_red);
                } else if (emc_cert != null && !emc_cert.equals("") && !emc_cert.equals("NA")) {
                    emc_certCell.setCellValue(emc_cert);
                    emc_certCell.setCellStyle(style_green);
                } else {
                    emc_certCell.setCellValue("NA");
                    emc_certCell.setCellStyle(style_border);
                }

                emc_tr = rs.getString(22);
                XSSFCell emc_trCell = row.createCell(21);
                if (emc_tr != null && emc_tr.contains("MISSING")) {
                    emc_trCell.setCellValue("MISSING");
                    emc_trCell.setCellStyle(style_red);
                } else if (emc_tr != null && !emc_tr.equals("") && !emc_tr.equals("NA")) {
                    emc_trCell.setCellValue(emc_tr);
                    emc_trCell.setCellStyle(style_green);
                } else {
                    emc_trCell.setCellValue("NA");
                    emc_trCell.setCellStyle(style_border);
                }

                rf_ce = rs.getString(23);
                XSSFCell rf_ceCell = row.createCell(22);
                if (rf_ce != null) {
                    rf_ceCell.setCellValue(rf_ce);
                    rf_ceCell.setCellStyle(style_green);
                } else {
                    rf_ceCell.setCellValue("NA");
                    rf_ceCell.setCellStyle(style_border);
                }

                rf_cert = rs.getString(24);
                XSSFCell rf_certCell = row.createCell(23);
                if (rf_cert != null && rf_cert.contains("MISSING")) {
                    rf_certCell.setCellValue("MISSING");
                    rf_certCell.setCellStyle(style_red);
                } else if (rf_cert != null && !rf_cert.equals("") && !rf_cert.equals("NA")) {
                    rf_certCell.setCellValue(rf_cert);
                    rf_certCell.setCellStyle(style_green);
                } else {
                    rf_certCell.setCellValue("NA");
                    rf_certCell.setCellStyle(style_border);
                }

                rf_tr = rs.getString(25);
                XSSFCell rf_trCell = row.createCell(24);
                if (rf_tr != null && rf_tr.contains("MISSING")) {
                    rf_trCell.setCellValue("MISSING");
                    rf_trCell.setCellStyle(style_red);
                } else if (rf_tr != null && !rf_tr.equals("") && !rf_tr.equals("NA")) {
                    rf_trCell.setCellValue(rf_tr);
                    rf_trCell.setCellStyle(style_green);
                } else {
                    rf_trCell.setCellValue("NA");
                    rf_trCell.setCellStyle(style_border);
                }

                cpd_dir = rs.getString(26);
                XSSFCell cpd_dirCell = row.createCell(25);
                if (cpd_dir != null) {
                    cpd_dirCell.setCellValue(cpd_dir);
                    cpd_dirCell.setCellStyle(style_green);
                } else {
                    cpd_dirCell.setCellValue("NA");
                    cpd_dirCell.setCellStyle(style_border);
                }

                cpd_ce = rs.getString(27);
                XSSFCell cpd_ceCell = row.createCell(26);
                if (cpd_ce != null && cpd_ce.contains("MISSING")) {
                    cpd_ceCell.setCellValue("MISSING");
                    cpd_ceCell.setCellStyle(style_red);
                } else if (cpd_ce != null && !cpd_ce.equals("") && !cpd_ce.equals("NA")) {
                    cpd_ceCell.setCellValue(cpd_ce);
                    cpd_ceCell.setCellStyle(style_green);
                } else {
                    cpd_ceCell.setCellValue("NA");
                    cpd_ceCell.setCellStyle(style_border);
                }

                cpd_tr = rs.getString(28);
                XSSFCell cpd_trCell = row.createCell(27);
                if (cpd_tr != null && cpd_tr.contains("MISSING")) {
                    cpd_trCell.setCellValue("MISSING");
                    cpd_trCell.setCellStyle(style_red);
                } else if (cpd_tr != null && !cpd_tr.equals("") && !cpd_tr.equals("NA")) {
                    cpd_trCell.setCellValue(cpd_tr);
                    cpd_trCell.setCellStyle(style_green);
                } else {
                    cpd_trCell.setCellValue("NA");
                    cpd_trCell.setCellStyle(style_border);
                }

                eup_ce = rs.getString(29);
                XSSFCell eup_ceCell = row.createCell(28);
                if (eup_ce != null) {
                    eup_ceCell.setCellValue(eup_ce);
                    eup_ceCell.setCellStyle(style_green);
                } else {
                    eup_ceCell.setCellValue("NA");
                    eup_ceCell.setCellStyle(style_border);
                }

                eup_tr = rs.getString(30);
                XSSFCell eup_trCell = row.createCell(29);
                if (eup_tr != null && eup_tr.contains("MISSING")) {
                    eup_trCell.setCellValue("MISSING");
                    eup_trCell.setCellStyle(style_red);
                } else if (eup_tr != null && !eup_tr.equals("") && !eup_tr.equals("NA")) {
                    eup_trCell.setCellValue(eup_tr);
                    eup_trCell.setCellStyle(style_green);
                } else {
                    eup_trCell.setCellValue("NA");
                    eup_trCell.setCellStyle(style_border);
                }

                flux_tr = rs.getString(31);
                XSSFCell flux_trCell = row.createCell(30);
                if (flux_tr != null && flux_tr.contains("MISSING")) {
                    flux_trCell.setCellValue("MISSING");
                    flux_trCell.setCellStyle(style_red);
                } else if (flux_tr != null) {
                    flux_trCell.setCellValue(eup_tr);
                    flux_trCell.setCellStyle(style_green);
                } else {
                    flux_trCell.setCellValue("NA");
                    flux_trCell.setCellStyle(style_border);
                }

                rohs_tr = rs.getString(32);
                XSSFCell rohs_trCell = row.createCell(31);
                if (rohs_tr != null && rohs_tr.contains("MISSING")) {
                    rohs_trCell.setCellValue("MISSING");
                    rohs_trCell.setCellStyle(style_red);
                } else if (rohs_tr != null) {
                    rohs_trCell.setCellValue(rohs_tr);
                    rohs_trCell.setCellStyle(style_green);
                } else {
                    rohs_trCell.setCellValue("NA");
                    rohs_trCell.setCellStyle(style_border);
                }

                reach_ce = rs.getString(33);
                XSSFCell reach_ceCell = row.createCell(32);
                if (reach_ce != null && reach_ce.contains("MISSING")) {
                    reach_ceCell.setCellValue("MISSING");
                    reach_ceCell.setCellStyle(style_red);
                } else if (reach_ce != null) {
                    reach_ceCell.setCellValue(reach_ce);
                    reach_ceCell.setCellStyle(style_green);
                } else {
                    reach_ceCell.setCellValue("NA");
                    reach_ceCell.setCellStyle(style_border);
                }

                pah_ce = rs.getString(34);
                XSSFCell pah_ceCell = row.createCell(33);
                if (pah_ce != null && pah_ce.contains("MISSING")) {
                    pah_ceCell.setCellValue("MISSING");
                    pah_ceCell.setCellStyle(style_red);
                } else if (pah_ce != null) {
                    pah_ceCell.setCellValue(pah_ce);
                    pah_ceCell.setCellStyle(style_green);
                } else {
                    pah_ceCell.setCellValue("NA");
                    pah_ceCell.setCellStyle(style_border);
                }

                vds_ce = rs.getString(35);
                XSSFCell vds_ceCell = row.createCell(34);
                if (vds_ce != null) {
                    vds_ceCell.setCellValue(vds_ce);
                    vds_ceCell.setCellStyle(style_green);
                } else {
                    vds_ceCell.setCellValue("NA");
                    vds_ceCell.setCellStyle(style_border);
                }

                vds_tr = rs.getString(36);
                XSSFCell vds_trCell = row.createCell(35);
                if (vds_tr != null) {
                    vds_trCell.setCellValue(vds_tr);
                    vds_trCell.setCellStyle(style_green);
                } else {
                    vds_trCell.setCellValue("NA");
                    vds_trCell.setCellStyle(style_border);
                }

                nf_ce = rs.getString(37);
                XSSFCell nf_ceCell = row.createCell(36);
                if (nf_ce != null) {
                    nf_ceCell.setCellValue(nf_ce);
                    nf_ceCell.setCellStyle(style_green);
                } else {
                    nf_ceCell.setCellValue("NA");
                    nf_ceCell.setCellStyle(style_border);
                }

                nf_tr = rs.getString(38);
                XSSFCell nf_trCell = row.createCell(37);
                if (nf_tr != null) {
                    nf_trCell.setCellValue(nf_tr);
                    nf_trCell.setCellStyle(style_green);
                } else {
                    nf_trCell.setCellValue("NA");
                    nf_trCell.setCellStyle(style_border);
                }

                bosec_ce = rs.getString(39);
                XSSFCell bosec_ceCell = row.createCell(38);
                if (bosec_ce != null) {
                    bosec_ceCell.setCellValue(bosec_ce);
                    bosec_ceCell.setCellStyle(style_green);
                } else {
                    bosec_ceCell.setCellValue("NA");
                    bosec_ceCell.setCellStyle(style_border);
                }

                komo_ce = rs.getString(40);
                XSSFCell komo_ceCell = row.createCell(39);
                if (komo_ce != null) {
                    komo_ceCell.setCellValue(komo_ce);
                    komo_ceCell.setCellStyle(style_green);
                } else {
                    komo_ceCell.setCellValue("NA");
                    komo_ceCell.setCellStyle(style_border);
                }

                kk_ce = rs.getString(41);
                XSSFCell kk_ceCell = row.createCell(40);
                if (kk_ce != null) {
                    kk_ceCell.setCellValue(kk_ce);
                    kk_ceCell.setCellStyle(style_green);
                } else {
                    kk_ceCell.setCellValue("NA");
                    kk_ceCell.setCellStyle(style_border);
                }

                batt_m = rs.getString(42);
                XSSFCell batt_mCell = row.createCell(41);
                if (batt_m != null && batt_m.contains("MISSING")) {
                    batt_mCell.setCellValue("MISSING");
                    batt_mCell.setCellStyle(style_red);
                } else if (batt_m != null) {
                    batt_mCell.setCellValue(batt_m);
                    batt_mCell.setCellStyle(style_green);
                } else {
                    batt_mCell.setCellValue("NA");
                    batt_mCell.setCellStyle(style_border);
                }

                batt_tr2 = rs.getString(43);
                XSSFCell batt_tr2Cell = row.createCell(42);
                if (batt_tr2 != null && batt_tr2.contains("MISSING")) {
                    batt_tr2Cell.setCellValue("MISSING");
                    batt_tr2Cell.setCellStyle(style_red);
                } else if (batt_tr2 != null) {
                    batt_tr2Cell.setCellValue(batt_tr2);
                    batt_tr2Cell.setCellStyle(style_green);
                } else {
                    batt_tr2Cell.setCellValue("NA");
                    batt_tr2Cell.setCellStyle(style_border);
                }

                doc = rs.getInt(44);
                XSSFCell docCell = row.createCell(43);
                if (doc == 0) {
                    docCell.setCellValue("MISSING");
                    docCell.setCellStyle(style_red);
                } else {
                    docCell.setCellValue("Declaration");
                    docCell.setCellStyle(style_green);
                }

                doi = rs.getInt(45);
                XSSFCell doiCell = row.createCell(44);
                if (doi == 0) {
                    doiCell.setCellValue("MISSING");
                    doiCell.setCellStyle(style_red);
                } else {
                    doiCell.setCellValue("Declaration");
                    doiCell.setCellStyle(style_green);
                }

                remarks = rs.getString(46);
                XSSFCell remarksCell = row.createCell(45);
                if (remarks != null) {
                    remarksCell.setCellValue(remarks);
                    remarksCell.setCellStyle(style_orange);
                } else {
                    remarksCell.setCellStyle(style_border);
                }

                remarks_auth = rs.getString(47);
                XSSFCell remarks_authCell = row.createCell(46);
                if (remarks_auth != null) {
                    remarks_authCell.setCellValue(remarks_auth);
                    remarks_authCell.setCellStyle(style_orange);
                } else {
                    remarks_authCell.setCellStyle(style_border);
                }

                return_place = rs.getString(48);
                XSSFCell return_placeCell = row.createCell(47);
                if (return_place != null) {
                    return_placeCell.setCellValue(return_place);
                    return_placeCell.setCellStyle(style_border);
                } else {
                    return_placeCell.setCellStyle(style_border);
                }
                
                mod_date = rs.getString(49);
                XSSFCell mod_dateCell = row.createCell(48);
                if (mod_date != null) {
                    mod_dateCell.setCellValue(mod_date);
                    mod_dateCell.setCellStyle(style_border);
                } else {
                    mod_dateCell.setCellStyle(style_border);
                }

                mod_who = rs.getString(50);
                XSSFCell mod_whoCell = row.createCell(49);
                if (mod_who != null) {
                    mod_whoCell.setCellValue(mod_who);
                    mod_whoCell.setCellStyle(style_border);
                } else {
                    mod_whoCell.setCellStyle(style_border);
                }

                rownr += 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Update_status.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Utils.closeDB(rs, st, con);
        }
        fis.close();
        FileOutputStream fos = new FileOutputStream(new File(excelname));
        wb.write(fos);
        fos.close();



    }
}
