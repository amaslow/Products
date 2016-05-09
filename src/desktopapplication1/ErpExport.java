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
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class ErpExport {

    private static int a;
    private static String kindl;
    int col = 46;
    static int zmienna;

    public static List<desktopapplication1.Items> QueryItem() {

        int b = zmienna;

        switch (b) {
            case 10:
                kindl = "'LED'";
                break;
            case 11:
                kindl = "'CFL'";
                break;
            case 12:
                kindl = "'HAL'";
                break;
            case 21:
                kindl = "'LED' or KIND_BULB = 'CFL'";
                break;
            case 22:
                kindl = "'LED' or KIND_BULB = 'HAL'";
                break;
            case 23:
                kindl = "'CFL' or KIND_BULB = 'HAL'";
                break;
            case 33:
                kindl = "'LED' or KIND_BULB = 'CFL' or KIND_BULB = 'HAL'";
                break;
            case 100:
                kindl = "'Luminaire'";
                break;
            case 110:
                kindl = "'LED' or KIND_BULB = 'Luminaire'";
                break;
            case 111:
                kindl = "'CFL' or KIND_BULB = 'Luminaire'";
                break;
            case 112:
                kindl = "'HAL' or KIND_BULB = 'Luminaire'";
                break;
            case 121:
                kindl = "'LED' or KIND_BULB = 'CFL' or KIND_BULB = 'Luminaire'";
                break;
            case 122:
                kindl = "'LED' or KIND_BULB = 'HAL' or KIND_BULB = 'Luminaire'";
                break;
            case 123:
                kindl = "'CFL' or KIND_BULB = 'HAL' or KIND_BULB = 'Luminaire'";
                break;
            case 133:
                kindl = "'LED' or KIND_BULB = 'CFL' or KIND_BULB = 'HAL' or KIND_BULB = 'Luminaire'";
                break;
        }

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String sql = "SELECT sap,item,descr_en,brand,kind_bulb,special_use,"
                + "voltage,ampere,wattage,lumen,lifetime,swicyc,kelvin,enclas,star60,dimmer,beam,ra,compar,fittin,kwik,accent,dimension_fi,dimension_l,dimension_d,"
                + "wattage_rated,lumen_rated,lifetime_rated,colour,beam_r,indoor,power_factor,lumen_factor,start_time,lichtb,color_cons,candela,"
                + "spectrum,shape,led_number,led_type,uv,item_bulb,int_led"
                + " FROM elro.items WHERE KIND_BULB = " + kindl + ";";
        con = Utils.getConnection();
        List<Items> listERP = new ArrayList<Items>();

        try {
            st = con.createStatement();

            rs = st.executeQuery(sql);
            while (rs.next()) {
                Items model = new Items();
                model.setSap(rs.getString(1));
                model.setItem(rs.getString(2));
                model.setDescrEn(rs.getString(3));                
                model.setBrand(rs.getString(4));
                model.setKind_bulb(rs.getString(5));
                model.setSpecial_use(rs.getString(6));

                model.setVoltage(rs.getString(7));
                model.setAmpere(rs.getString(8));
                model.setWattage(rs.getString(9));
                model.setLumen(rs.getString(10));
                model.setLifetime(rs.getString(11));
                model.setSwicyc(rs.getString(12));
                model.setKelvin(rs.getString(13));
                model.setEnclas(rs.getString(14));
                model.setStar60(rs.getString(15));
                model.setDimmer(rs.getString(16));
                model.setBeam(rs.getString(17));
                model.setRa(rs.getString(18));
                model.setCompar(rs.getString(19));
                model.setFittin(rs.getString(20));
                model.setKwik(rs.getString(21));
                model.setAaccent(rs.getBoolean(22));
                model.setDimension_Fi(rs.getString(23));
                model.setDimension_L(rs.getString(24));
                model.setDimension_D(rs.getString(25));

                model.setWattage_Rated(rs.getString(26));
                model.setLumen_Rated(rs.getString(27));
                model.setLifetime_Rated(rs.getString(28));
                model.setColour(rs.getString(29));
                model.setBeam_R(rs.getString(30));
                model.setIndoor(rs.getString(31));
                model.setPower_Factor(rs.getString(32));
                model.setLumen_Factor(rs.getString(33));
                model.setStart_Time(rs.getString(34));
                model.setLichtb(rs.getString(35));
                model.setColor_Cons(rs.getString(36));
                model.setCandela(rs.getString(37));

                model.setSpectrum(rs.getString(38));
                model.setShape(rs.getString(39));
                model.setLed_Number(rs.getString(40));
                model.setLed_Type(rs.getString(41));
                model.setUV(rs.getBoolean(42));
                model.setItem_Bulb(rs.getString(43));
                model.setInt_Led(rs.getBoolean(44));

                listERP.add(model);
            }
        } catch (SQLException e) {
        } finally {
            Utils.closeDB(rs, st, con);
        }
        return listERP;
    }

    public void CreateExcel(int a) {
// Create a workbook 

        zmienna = a;
        String lampa = null;
        switch (zmienna) {
            case 10:
                lampa = "LED";
                break;
            case 11:
                lampa = "CFL";
                break;
            case 12:
                lampa = "HAL";
                break;
            case 21:
                lampa = "LED_CFL";
                break;
            case 22:
                lampa = "LED_HAL";
                break;
            case 23:
                lampa = "CFL_HAL";
                break;
            case 33:
                lampa = "LED_CFL_HAL";
                break;
            case 100:
                lampa = "Luminaire";
                break;
            case 110:
                lampa = "LED_Luminaire";
                break;
            case 111:
                lampa = "CFL_Luminaire";
                break;
            case 112:
                lampa = "HAL_Luminaire";
                break;
            case 121:
                lampa = "LED_CFL_Luminaire";
                break;
            case 122:
                lampa = "LED_HAL_Luminaire";
                break;
            case 123:
                lampa = "CFL_HAL_Luminaire";
                break;
            case 133:
                lampa = "LED_CFL_HAL_Luminaire";
                break;
        }
        if (a > 0) {
            HSSFWorkbook workBook = new HSSFWorkbook();
// Create a worksheet named: 
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            String d = String.valueOf(dateFormat.format(date));
            HSSFSheet sheet = workBook.createSheet("ErP_" + d + "_" + lampa);

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

            HSSFCellStyle styleTitlePack = workBook.createCellStyle();
            styleTitlePack.setBorderTop((short) 1); // 1:single lines border
            styleTitlePack.setBorderLeft((short) 1);
            styleTitlePack.setBorderRight((short) 1);
            styleTitlePack.setBorderBottom((short) 1); // 6:double line border
            styleTitlePack.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
            styleTitlePack.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styleTitlePack.setWrapText(true);
            styleTitlePack.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont fontTitlePack = workBook.createFont();
            fontTitlePack.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            styleTitlePack.setFont(fontTitlePack);

            HSSFCellStyle styleTitleWeb = workBook.createCellStyle();
            styleTitleWeb.setBorderTop((short) 1); // 1:single lines border
            styleTitleWeb.setBorderLeft((short) 1);
            styleTitleWeb.setBorderRight((short) 1);
            styleTitleWeb.setBorderBottom((short) 1); // 6:double line border
            styleTitleWeb.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
            styleTitleWeb.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styleTitleWeb.setWrapText(true);
            styleTitleWeb.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont fontTitleWeb = workBook.createFont();
            fontTitleWeb.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            styleTitleWeb.setFont(fontTitleWeb);

            HSSFCellStyle styleTitleExtra = workBook.createCellStyle();
            styleTitleExtra.setBorderTop((short) 1); // 1:single lines border
            styleTitleExtra.setBorderLeft((short) 1);
            styleTitleExtra.setBorderRight((short) 1);
            styleTitleExtra.setBorderBottom((short) 1); // 6:double line border
            styleTitleExtra.setFillForegroundColor(HSSFColor.YELLOW.index);
            styleTitleExtra.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styleTitleExtra.setWrapText(true);
            styleTitleExtra.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            HSSFFont fontTitleExtra = workBook.createFont();
            fontTitleExtra.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            styleTitleExtra.setFont(fontTitleExtra);

            HSSFCellStyle styleLen = workBook.createCellStyle();
            styleLen.setBorderTop((short) 1);
            styleLen.setBorderLeft((short) 1);
            styleLen.setBorderRight((short) 1);
            styleLen.setBorderBottom((short) 1);
            styleLen.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            styleLen.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styleLen.setWrapText(true);
            styleLen.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            HSSFFont fontLen = workBook.createFont();
            fontLen.setFontHeightInPoints((short) 8);
            fontLen.setItalic(true);
            styleLen.setFont(fontLen);

            for (int x = 0; x < col; x++) {
                sheet.setColumnWidth(x, 3500);
            }

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
            cellTitle[0].setCellValue("ErP " + lampa + " information");

// Create a cell, starting from 4 
            HSSFRow row0 = sheet.createRow((short) 3);
            row0.setHeight((short) 650);
// Construct an array to set the cell after the line 
            HSSFCell cell[] = new HSSFCell[col];
            for (int i = 0; i < cell.length; i++) {
                cell[i] = row0.createCell(i);
                //    sheet.autoSizeColumn((short) i);
                if (i < 6) {
                    cell[i].setCellStyle(styleTitleDescr);
                } else if (i > 5 && i < 27) {
                    cell[i].setCellStyle(styleTitlePack);
                } else if (i > 26 && i < 40) {
                    cell[i].setCellStyle(styleTitleWeb);
                } else {
                    cell[i].setCellStyle(styleTitleExtra);
                }
            }
            cell[0].setCellValue("Material");
            cell[1].setCellValue("Old material number");
            cell[2].setCellValue("Material Description");
            cell[3].setCellValue("Brand Name");
            cell[4].setCellValue("Type of bulb");
            cell[5].setCellValue("Special use");

            cell[6].setCellValue("Main Voltage (V)");
            cell[7].setCellValue("Nominal current (mA)");
            cell[8].setCellValue("Bulb wattage (W)");
            cell[9].setCellValue("Light output in Lumen (lm)");
            cell[10].setCellValue("Lifetime hours nominal (h)");
            cell[11].setCellValue("Switching cycles");
            cell[12].setCellValue("Colour temperature rated (K)");
            cell[13].setCellValue("Energy class");
            cell[14].setCellValue("Start time to 60%");
            cell[15].setCellValue("Dimmable");
            cell[16].setCellValue("Beam angle (°)");
            cell[17].setCellValue("Colourrendering index (rated)>");
            cell[18].setCellValue("Equivalent to (W)");
            cell[19].setCellValue("Type of fitting");
            cell[20].setCellValue("Mercury (mg) <");
            cell[21].setCellValue("Suitable for accent lighting");
            cell[22].setCellValue("Lamp dimensions larg.diamet.cm");
            cell[23].setCellValue("Lamp dimensions length (cm)");
            cell[24].setCellValue("Lamp dimensions depth (cm)");
            cell[25].setCellValue("Mercury instructions");
            cell[26].setCellValue("Mercury instructions-CLEAN-UP");

            cell[27].setCellValue("Rated Power (W)");
            cell[28].setCellValue("Light output rated (lm)");
            cell[29].setCellValue("Lifetime hours rated (h)");
            cell[30].setCellValue("Colour of light");
            cell[31].setCellValue("Beam angle rated (°)");
            cell[32].setCellValue("Indoor/outdoor use");
            cell[33].setCellValue("Powerfactor Pf (rated) >");
            cell[34].setCellValue("Lumen maintenance factor LMF >");
            cell[35].setCellValue("Start time (s) <");
            cell[36].setCellValue("Lamp survival factor LSF >");
            cell[37].setCellValue("Colour consistency LED >");
            cell[38].setCellValue("Rated peak intensitycandela cd");
            cell[39].setCellValue("Spectr. power distr. 180-800nm");

            cell[40].setCellValue("Shape bulb");
            cell[41].setCellValue("Number of LEDs");
            cell[42].setCellValue("LED type");
            cell[43].setCellValue("UV block");
            cell[44].setCellValue("Included bulb");
            cell[45].setCellValue("Integrated LED");

// Create a cell, starting from 5 ENGLISH
            HSSFRow row1 = sheet.createRow((short) 4);
            row1.setHeight((short) 650);
// Construct an array to set the cell after the line 
            HSSFCell cell1[] = new HSSFCell[col];
            for (int i = 0; i < cell1.length; i++) {
                cell1[i] = row1.createCell(i);
                cell1[i].setCellStyle(styleLen);
            }
            cell1[0].setCellValue("SAP number");
            cell1[1].setCellValue("Item number");
            cell1[2].setCellValue("Item description");
            cell1[3].setCellValue("Brand name");
            cell1[4].setCellValue("Bulb type");
            cell1[5].setCellValue("Special use");

            cell1[6].setCellValue("Nominal voltage");
            cell1[7].setCellValue("Nominal current");
            cell1[8].setCellValue("Nominal lamp power");
            cell1[9].setCellValue("Nominal useful luminous flux");
            cell1[10].setCellValue("Nominal life time");
            cell1[11].setCellValue("Number of switching cycles");
            cell1[12].setCellValue("Color temperature");
            cell1[13].setCellValue("Energy efficiency class");
            cell1[14].setCellValue("Warm-up time to 60% (95% for LED) of the full light output");
            cell1[15].setCellValue("Dimmable");
            cell1[16].setCellValue("Nominal beam angle");
            cell1[17].setCellValue("Color rendering Ra");
            cell1[18].setCellValue("Equivalent power");
            cell1[19].setCellValue("Base/fitting");
            cell1[20].setCellValue("Mercury content");
            cell1[21].setCellValue("Suitable for accent lighting");
            cell1[22].setCellValue("Dimension: diameter");
            cell1[23].setCellValue("Dimension: length");
            cell1[24].setCellValue("Dimension: depth");
            cell1[25].setCellValue("Compact fluorescent lamps have to be treated as special waste, they must be taken to your "
                    + "local waste facilities for recycling. The European Lighting Industry has set up an infrastructure, capable "
                    + "of recycling mercury, other metals, glass etc.");
            cell1[26].setCellValue("Breaking a lamp is extremely unlikely to have any impact on your health. If a lamp breaks, "
                    + "ventilate the room for 30 minutes and remove the parts, preferably with gloves. Put them in a closed plastic "
                    + "bag and take it to your local waste facilities for recycling. Do not use a vacuum cleaner.");

            cell1[27].setCellValue("Rated power");
            cell1[28].setCellValue("Rated luminous flux");
            cell1[29].setCellValue("Rated life time");
            cell1[30].setCellValue("Colour name");
            cell1[31].setCellValue("Rated beam angle");
            cell1[32].setCellValue("Indoor/outdoor use");
            cell1[33].setCellValue("Power factor");
            cell1[34].setCellValue("Lumen maintenace factor");
            cell1[35].setCellValue("Starting time");
            cell1[36].setCellValue("Lamp survival factor");
            cell1[37].setCellValue("Color consistency (LED only)");
            cell1[38].setCellValue("Rated peak intensity in candela");
            cell1[39].setCellValue("Spectral power distribution in the range 180-800nm");

            cell1[40].setCellValue("Shape");
            cell1[41].setCellValue("Number of LEDs");
            cell1[42].setCellValue("LED type");
            cell1[43].setCellValue("UV block");
            cell1[44].setCellValue("Included bulb");
            cell1[45].setCellValue("Integrated LED");

// Create a cell, starting from 6 DUTCH
            HSSFRow row2 = sheet.createRow((short) 5);
            row2.setHeight((short) 650);
// Construct an array to set the cell after the second line 
            HSSFCell cell2[] = new HSSFCell[col];
            for (int i = 0; i < cell2.length; i++) {
                cell2[i] = row2.createCell(i);
                cell2[i].setCellStyle(styleLen);
            }
            cell2[0].setCellValue("SAP nummer");
            cell2[1].setCellValue("Artikel");
            cell2[2].setCellValue("Artikelomschrijving");
            cell2[3].setCellValue("Merknaam");
            cell2[4].setCellValue("Lamptype");
            cell2[5].setCellValue("Speciaal gebruik");

            cell2[6].setCellValue("Nominaal spanning");
            cell2[7].setCellValue("Nominaal stroom");
            cell2[8].setCellValue("Nominaal vermogen");
            cell2[9].setCellValue("De nominale nuttige lichtstroom");
            cell2[10].setCellValue("De nominale levensduur");
            cell2[11].setCellValue("Het aantal schakelcycli");
            cell2[12].setCellValue("De kleurtemperatuur");
            cell2[13].setCellValue("Energieklasse");
            cell2[14].setCellValue("De opwarmingstijd tot 60% (95% voor LED) van de volledige lichtopbrengst");
            cell2[15].setCellValue("Dimbaar");
            cell2[16].setCellValue("De nominale hoek van de lichtbundel");
            cell2[17].setCellValue("Kleurweergave");
            cell2[18].setCellValue("Vergelijkingswattage");
            cell2[19].setCellValue("Base/fitting");
            cell2[20].setCellValue("Kwikgehalte");
            cell2[21].setCellValue("Geschikt voor accentverlichting");
            cell2[22].setCellValue("De afmetingen: diameter");
            cell2[23].setCellValue("De afmetingen: lengte");
            cell2[24].setCellValue("De afmetingen: diepte");
            cell2[25].setCellValue(" Compacte TL-lampen moeten worden behandeld als bijzonder afval en moeten worden aangeboden bij de "
                    + "plaatselijke afvalverwijdering voor recycling. De Europese verlichtingsindustrie heeft een infrastructuur "
                    + "opgezet voor het recyclen van kwik, andere metalen, glas enz.");
            cell2[26].setCellValue("Het is hoogst onwaarschijnlijk dat het breken van een lamp invloed heeft op uw gezondheid. "
                    + "Als een lamp breekt, ventileert u de kamer gedurende 30 minuten en ruimt u de scherven op, liefst met "
                    + "handschoenen aan. Doe ze in een gesloten plastic zak en bied deze aan bij de plaatselijke afvalverwijdering "
                    + "voor recycling. Gebruik geen stofzuiger.");

            cell2[27].setCellValue("Opgegeven vermogen");
            cell2[28].setCellValue("Opgegeven nuttige lichtstroom");
            cell2[29].setCellValue("Opgegeven levensduur");
            cell2[30].setCellValue("Kleurnaam");
            cell2[31].setCellValue("Opgegeven hoek van de lichtbundel");
            cell2[32].setCellValue("Binnen / buitengebruik");
            cell2[33].setCellValue("lampvermogen/arbeidsfactor");
            cell2[34].setCellValue("Lumenbehoudfactor");
            cell2[35].setCellValue("Ontbrandingstijd");
            cell2[36].setCellValue("Lampoverlevingsfactor");
            cell2[37].setCellValue("Kleurconsistentie (uitsluitend voor leds)");
            cell2[38].setCellValue("Opgegeven pieksterkte in candela");
            cell2[39].setCellValue("Spectrale distributie in het bereik van 180-800 nm");

            cell2[40].setCellValue("Model");
            cell2[41].setCellValue("Aantal LEDs");
            cell2[42].setCellValue("LED type");
            cell2[43].setCellValue("UV blok");
            cell2[44].setCellValue("Inclusief lamp");
            cell2[45].setCellValue("Integreerde LED");

// Create a cell, starting from 7 GERMAN
            HSSFRow row3 = sheet.createRow((short) 6);
            row3.setHeight((short) 650);
// Construct an array to set the cell after the third line 
            HSSFCell cell3[] = new HSSFCell[col];
            for (int i = 0; i < cell3.length; i++) {
                cell3[i] = row3.createCell(i);
                cell3[i].setCellStyle(styleLen);
            }
            cell3[0].setCellValue("SAP-Nummer");
            cell3[1].setCellValue("Artikelnummer");
            cell3[2].setCellValue("Artikelbeschreibung");
            cell3[3].setCellValue("Markenname");
            cell3[4].setCellValue("Lampentyp");
            cell3[5].setCellValue("Sondernutzung");

            cell3[6].setCellValue("Nennspannung");
            cell3[7].setCellValue("Nennstrom");
            cell3[8].setCellValue("Lampennennleistung");
            cell3[9].setCellValue("Nomineller Nutzlichtstrom");
            cell3[10].setCellValue("Nennlebensdauer");
            cell3[11].setCellValue("Zahl der Schaltzyklen");
            cell3[12].setCellValue("Farbtemperatur");
            cell3[13].setCellValue("Etikett");
            cell3[14].setCellValue("Anlaufzeit bis 60% (95% für LED) des vollen Lichtleistung");
            cell3[15].setCellValue("Dimmern");
            cell3[16].setCellValue("Nomineller Halbwertswinkel");
            cell3[17].setCellValue("Farbwiedergabe");
            cell3[18].setCellValue("Äquivalenz Leistung");
            cell3[19].setCellValue("Einbau");
            cell3[20].setCellValue("Quecksilbergehalt");
            cell3[21].setCellValue("Geeignet für eine Akzentbeleuchtung");
            cell3[22].setCellValue("Abmessungen: Durchmesser");
            cell3[23].setCellValue("Abmessungen: Länge");
            cell3[24].setCellValue("Abmessungen: Tiefe");
            cell3[25].setCellValue("Kompaktleuchtstofflampen sind als Sondermüll zu behandeln und müssen bei der örtlichen "
                    + "Müllsammelstelle zur Wiederverwertung abgegeben werden. Die europäische Beleuchtungsindustrie hat die "
                    + "nötige Infrastruktur zur Wiederverwertung von Quecksilber, anderen Metallen, Glas usw. eingerichtet.");
            cell3[26].setCellValue("Es ist sehr unwahrscheinlich, dass das Zerbrechen einer Lampe Auswirkungen auf Ihre "
                    + "Gesundheit hat. Wenn eine Lampe zerbricht, lüften Sie den Raum 30 Minuten lang, und entfernen Sie "
                    + "die Bruchstücke am besten mit Handschuhen. Geben Sie die Stücke in eine Plastiktüte, und bringen Sie sie "
                    + "zur örtlichen Sammelstelle. Verwenden Sie keinen Staubsauger.");

            cell3[27].setCellValue("Bemessungswert der Leistungsaufnahme");
            cell3[28].setCellValue("Bemessungsnutzlichtstrom");
            cell3[29].setCellValue("Bemessungslebensdauer");
            cell3[30].setCellValue("Farbbezeichnung");
            cell3[31].setCellValue("Bemessungshalbwertswinkel");
            cell3[32].setCellValue("Innenbereich/Außenbereich");
            cell3[33].setCellValue("Leistungsfaktor");
            cell3[34].setCellValue("Lampenlichtstromerhalt");
            cell3[35].setCellValue("Zündzeitpunkt");
            cell3[36].setCellValue("Lampenlebensdauerfaktor");
            cell3[37].setCellValue("Farbkonsistenz (nur für Leuchtdioden)");
            cell3[38].setCellValue("Bemessungsspitzenlichtstärke");
            cell3[39].setCellValue("Spektrale Strahlungsverteilung im Bereich 180-800nm");

            cell3[40].setCellValue("Form");
            cell3[41].setCellValue("Anzahl der LEDs");
            cell3[42].setCellValue("LED-Typ");
            cell3[43].setCellValue("UV-Block");
            cell3[44].setCellValue("Inklusive Leuchtmittel");
            cell3[45].setCellValue("Integrierte LED");

// Create a cell, starting from 8 FRENCH
            HSSFRow row4 = sheet.createRow((short) 7);
            row4.setHeight((short) 650);
// Construct an array to set the cell after the line 
            HSSFCell cell4[] = new HSSFCell[col];
            for (int i = 0; i < cell4.length; i++) {
                cell4[i] = row4.createCell(i);
                cell4[i].setCellStyle(styleLen);
            }
            cell4[0].setCellValue("Numéro de SAP");
            cell4[1].setCellValue("Numéro d'article");
            cell4[2].setCellValue("Description de l'article");
            cell4[3].setCellValue("Nom de marque");            
            cell4[4].setCellValue("Type d'ampoule");
            cell4[5].setCellValue("Usage spécial");

            cell4[6].setCellValue("La tension nominale");
            cell4[7].setCellValue("La courant nominale");
            cell4[8].setCellValue("La puissance nominale");
            cell4[9].setCellValue("Le flux lumineux utile nominal");
            cell4[10].setCellValue("La durée de vie nominale");
            cell4[11].setCellValue("Le nombre de cycles de commutation");
            cell4[12].setCellValue("La température de couleur");
            cell4[13].setCellValue("Classe d’efficacité énergétique");
            cell4[14].setCellValue("La durée de préchauffage nécessaire pour atteindre 60% (95% pour LED) du flux lumineux total");
            cell4[15].setCellValue("Gradable");
            cell4[16].setCellValue("L’angle de faisceau nominal");
            cell4[17].setCellValue("Le rendu des couleurs Ra");
            cell4[18].setCellValue("La puissance équivalente");
            cell4[19].setCellValue("Le raccord");
            cell4[20].setCellValue("La teneur en mercure");
            cell4[21].setCellValue("Convient pour l’éclairage d'accentuation");
            cell4[22].setCellValue("Les dimensions: diamètre");
            cell4[23].setCellValue("Les dimensions: longueur");
            cell4[24].setCellValue("Les dimensions: profondeur");
            cell4[25].setCellValue("Les ampoules fluocompactes appartiennent à la catégorie des déchets spéciaux, et doivent être "
                    + "confiées à votre déchetterie locale qui se chargera de leur recyclage. Le secteur européen de "
                    + "l'éclairage a créé une infrastructure capable de recycler le mercure, d'autres métaux, le verre, etc.");
            cell4[26].setCellValue("Il est extrêmement peu probable qu'une ampoule brisée ait un effet sur votre santé. Si une "
                    + "ampoule se brise, aérez la pièce pendant 30 minutes et ramassez les morceaux, de préférence en portant "
                    + "des gants. Placez-les dans un sac en plastique que vous fermerez et confierez à une déchetterie locale qui "
                    + "se chargera de leur recyclage. N'utilisez pas d'aspirateur.");

            cell4[27].setCellValue("La puissance assignée");
            cell4[28].setCellValue("Le flux lumineux utile assigné");
            cell4[29].setCellValue("La durée de vie assignée");
            cell4[30].setCellValue("Nom de la couleur");
            cell4[31].setCellValue("L’angle de faisceau assigné");
            cell4[32].setCellValue("Usage intérieur / extérieur");
            cell4[33].setCellValue("Le facteur de puissance");
            cell4[34].setCellValue("Le facteur de conservation du flux lumineux");
            cell4[35].setCellValue("La durée d’allumage");
            cell4[36].setCellValue("Facteur de survie des lampes");
            cell4[37].setCellValue("La constance des couleurs (uniquement pour les LED)");
            cell4[38].setCellValue("L’intensité maximale assignée en candela");
            cell4[39].setCellValue("La distribution spectrale de puissance dans la gamme 180-800 nm");

            cell4[40].setCellValue("La forme");
            cell4[41].setCellValue("Nombre de LED");
            cell4[42].setCellValue("Type de LED");
            cell4[43].setCellValue("Bloqueur d'UV");
            cell4[44].setCellValue("Inclus ampoule");
            cell4[45].setCellValue("LED intégré");            

// Create a cell, starting from 9 SPANISH
            HSSFRow row5 = sheet.createRow((short) 8);
            row5.setHeight((short) 650);
// Construct an array to set the cell after the line 
            HSSFCell cell5[] = new HSSFCell[col];
            for (int i = 0; i < cell5.length; i++) {
                cell5[i] = row5.createCell(i);
                cell5[i].setCellStyle(styleLen);
            }
            cell5[0].setCellValue("Número SAP");
            cell5[1].setCellValue("Número de artículo");
            cell5[2].setCellValue("Descripción del artículo");
            cell5[3].setCellValue("Marca de fábrica");
            cell5[4].setCellValue("Tipo de lámpara");
            cell5[5].setCellValue("De uso especial");

            cell5[6].setCellValue("Tensión nominal");
            cell5[7].setCellValue("Corriente nominal");
            cell5[8].setCellValue("Potencia nominal de la lámpara");
            cell5[9].setCellValue("Flujo luminoso útil nominal");
            cell5[10].setCellValue("Vida útil nominal");
            cell5[11].setCellValue("Número de ciclos de conmutación");
            cell5[12].setCellValue("Temperatura de color");
            cell5[13].setCellValue("Clase de eficiencia energética");
            cell5[14].setCellValue("Tiempo de calentamiento hasta el 60% (95% para LED) del flujo luminoso total");
            cell5[15].setCellValue("Dimmable");
            cell5[16].setCellValue("Ángulo del haz luminoso nominal");
            cell5[17].setCellValue("Rendimiento de color Ra");
            cell5[18].setCellValue("Potencia equivalente");
            cell5[19].setCellValue("Montaje de la lámpara");
            cell5[20].setCellValue("Contenido de mercurio");
            cell5[21].setCellValue("Idónea para la iluminación de acentuación");
            cell5[22].setCellValue("Dimensiones: diámetro");
            cell5[23].setCellValue("Dimensiones: longitud");
            cell5[24].setCellValue("Dimensiones: profundidad");
            cell5[25].setCellValue("Las lámparas fluorescentes compactas se deben tratar como residuos especiales y se deben "
                    + "llevar al punto limpio más cercano para su reciclaje. El sector europeo del alumbrado ha establecido una "
                    + "infraestructura para reciclar mercurio, otros metales, cristal, etc.");
            cell5[26].setCellValue(" Es muy poco probable que la rotura de una bombilla tenga consecuencias sobre su salud. "
                    + "Si se rompe una bombilla, ventile la habitación durante 30 minutos, retire los cristales y el resto de "
                    + "piezas (preferiblemente con guantes). Métalos en una bolsa de plástico cerrada y llévelos al punto limpio "
                    + "más cercano para su reciclaje. No utilice un aspirador.");

            cell5[27].setCellValue("La potencia asignada");
            cell5[28].setCellValue("El flujo luminoso útil asignado");
            cell5[29].setCellValue("La vida útil asignada");
            cell5[30].setCellValue("Nombre del color");
            cell5[31].setCellValue("El ángulo de haz luminoso asignado");
            cell5[32].setCellValue("Uso de interior/exterior");
            cell5[33].setCellValue("El factor de potencia");
            cell5[34].setCellValue("El factor de mantenimiento del flujo luminoso");
            cell5[35].setCellValue("El tiempo de encendido");
            cell5[36].setCellValue("Factor de supervivencia de la lámpara");
            cell5[37].setCellValue("La invariabilidad del color (únicamente para las LED)");
            cell5[38].setCellValue("La intensidad pico asignada en candelas");
            cell5[39].setCellValue("Distribución espectral de la potencia en el intervalo 180-800nm");

            cell5[40].setCellValue("Forma");
            cell5[41].setCellValue("Número de LEDs");
            cell5[42].setCellValue("Tipo de LED");
            cell5[43].setCellValue("Bloqueo de UV");
            cell5[44].setCellValue("Bombilla incluida");
            cell5[45].setCellValue("LED integrado");              

// Get the data from the database query 
            List<Items> list = ErpExport.QueryItem();

            if (list != null && list.size() > 0) {

// Loop listERP data 
                for (int i = 0; i < list.size(); i++) {
                    Items model = list.get(i);
                    HSSFRow dataRow = sheet.createRow(i + 9);
                    HSSFCell data[] = new HSSFCell[col];
                    HSSFCellStyle styleData = workBook.createCellStyle();
                    styleData.setBorderLeft((short) 1);
                    styleData.setBorderRight((short) 1);
                    for (int j = 0; j < data.length; j++) {
                        data[j] = dataRow.createCell(j);
                        //    sheet.autoSizeColumn((short) j);
                        data[j].setCellStyle(styleData);

                    }
                    data[0].setCellValue(model.getSap());
                    data[1].setCellValue(model.getItem());
                    data[2].setCellValue(model.getDescrEn());
                    data[3].setCellValue(model.getBrand());
                    data[4].setCellValue(model.getKind_bulb());
                    data[5].setCellValue(model.getSpecial_use());

                    data[6].setCellValue(model.getVoltage());
                    data[7].setCellValue(model.getAmpere());
                    data[8].setCellValue(model.getWattage());
                    data[9].setCellValue(model.getLumen());
                    data[10].setCellValue(model.getLifetime());
                    data[11].setCellValue(model.getSwicyc());
                    data[12].setCellValue(model.getKelvin());
                    data[13].setCellValue(model.getEnclas());
                    data[14].setCellValue(model.getStar60());
                    data[15].setCellValue(model.getDimmer());
                    data[16].setCellValue(model.getBeam());
                    data[17].setCellValue(model.getRa());
                    data[18].setCellValue(model.getCompar());
                    data[19].setCellValue(model.getFittin());
                    data[20].setCellValue(model.getKwik());
                    if (model.getAccent() == false) {
                        data[21].setCellValue("NA");
                    } else {
                        data[21].setCellValue("Yes");
                    }
                    data[22].setCellValue(model.getDimension_Fi());
                    data[23].setCellValue(model.getDimension_L());
                    data[24].setCellValue(model.getDimension_D());
                    if (model.getKwik()==null || model.getKwik().equals("NA")) {
                        data[25].setCellValue("NA");
                    } else {
                        data[25].setCellValue("YES");
                    }
                    if (model.getKwik()==null || model.getKwik().equals("NA")) {
                        data[26].setCellValue("NA");
                    } else {
                        data[26].setCellValue("YES");
                    }
                    data[27].setCellValue(model.getWattage_Rated());
                    data[28].setCellValue(model.getLumen_Rated());
                    data[29].setCellValue(model.getLifetime_Rated());
                    data[30].setCellValue(model.getColour());
                    data[31].setCellValue(model.getBeam_R());
                    data[32].setCellValue(model.getIndoor());
                    data[33].setCellValue(model.getPower_Factor());
                    data[34].setCellValue(model.getLumen_Factor());
                    data[35].setCellValue(model.getStart_Time());
                    data[36].setCellValue(model.getLichtb());
                    data[37].setCellValue(model.getColor_Cons());
                    data[38].setCellValue(model.getCandela());
                    data[39].setCellValue(model.getSpectrum());
                    data[40].setCellValue(model.getShape());
                    data[41].setCellValue(model.getLed_Number());
                    data[42].setCellValue(model.getLed_Type());
                    if (model.getUV() == false) {
                        data[43].setCellValue("NA");
                    } else {
                        data[43].setCellValue("Yes");
                    }
                    if (model.getItem_Bulb()==null) {
                        data[44].setCellValue("NA");
                    } else {
                        data[44].setCellValue(model.getItem_Bulb());
                    }
                    if (model.getInt_Led() == false) {
                        data[45].setCellValue("NA");
                    } else {
                        data[45].setCellValue("Yes");
                    }
                                        
                    try {
// Output to XLS file 
                        File file = new File("G://QC//ERP//ErP " + d + "_" + lampa + ".xls");
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

            File folder = new File("G://QC//ERP//");
            try {
                desktop.open(folder);
            } catch (IOException ex) {
                Logger.getLogger(ErpExport.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select at least one kind of lamp, please", "Export message", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
