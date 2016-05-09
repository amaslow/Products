package desktopapplication1;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author artur
 */
@Entity
@Table(name = "items", catalog = "ELRO", schema = "")
@NamedQueries({
    @NamedQuery(name = "Items.findAll", query = "SELECT i FROM Items i"),
    @NamedQuery(name = "Items.findById", query = "SELECT i FROM Items i WHERE i.id = :id"),
    @NamedQuery(name = "Items.findByHidden", query = "SELECT i FROM Items i WHERE i.hidden = :hidden"),
    @NamedQuery(name = "Items.findByItem", query = "SELECT i FROM Items i WHERE i.item = :item"),
    @NamedQuery(name = "Items.findBySap", query = "SELECT i FROM Items i WHERE i.sap = :sap"),
    @NamedQuery(name = "Items.findByComponent1", query = "SELECT i FROM Items i WHERE i.component1 = :component1"),
    @NamedQuery(name = "Items.findByComponent2", query = "SELECT i FROM Items i WHERE i.component2 = :component2"),
    @NamedQuery(name = "Items.findByComponent3", query = "SELECT i FROM Items i WHERE i.component3 = :component3"),
    @NamedQuery(name = "Items.findByComponent4", query = "SELECT i FROM Items i WHERE i.component4 = :component4"),
    @NamedQuery(name = "Items.findByComponent5", query = "SELECT i FROM Items i WHERE i.component5 = :component5"),
    @NamedQuery(name = "Items.findByComponent6", query = "SELECT i FROM Items i WHERE i.component6 = :component6"),
    @NamedQuery(name = "Items.findByComponent7", query = "SELECT i FROM Items i WHERE i.component7 = :component7"),
    @NamedQuery(name = "Items.findByComponent8", query = "SELECT i FROM Items i WHERE i.component8 = :component8"),
    @NamedQuery(name = "Items.findByComponent9", query = "SELECT i FROM Items i WHERE i.component9 = :component9"),
    @NamedQuery(name = "Items.findByComponent10", query = "SELECT i FROM Items i WHERE i.component10 = :component10"),
    @NamedQuery(name = "Items.findByBrand", query = "SELECT i FROM Items i WHERE i.brand = :brand"),
    @NamedQuery(name = "Items.findByVendor", query = "SELECT i FROM Items i WHERE i.vendor = :vendor"),
    @NamedQuery(name = "Items.findBySupplier", query = "SELECT i FROM Items i WHERE i.supplier = :supplier"),
    @NamedQuery(name = "Items.findBysItem", query = "SELECT i FROM Items i WHERE i.sItem = :sItem"),
    @NamedQuery(name = "Items.findByStatus", query = "SELECT i FROM Items i WHERE i.status = :status"),
    @NamedQuery(name = "Items.findByQCStatus", query = "SELECT i FROM Items i WHERE i.qc_status = :qc_status"),
    @NamedQuery(name = "Items.findByQMStatus", query = "SELECT i FROM Items i WHERE i.qm_status = :qm_status"),
    @NamedQuery(name = "Items.findByHierarchy", query = "SELECT i FROM Items i WHERE i.hierarchy = :hierarchy"),
    @NamedQuery(name = "Items.findByDescrEn", query = "SELECT i FROM Items i WHERE i.descrEn = :descrEn"),
    @NamedQuery(name = "Items.findByDescrFr", query = "SELECT i FROM Items i WHERE i.descrFr = :descrFr"),
    @NamedQuery(name = "Items.findByDescrDe", query = "SELECT i FROM Items i WHERE i.descrDe = :descrDe"),
    @NamedQuery(name = "Items.findByDescrNl", query = "SELECT i FROM Items i WHERE i.descrNl = :descrNl"),
    @NamedQuery(name = "Items.findByDescrEs", query = "SELECT i FROM Items i WHERE i.descrEs = :descrEs"),
    @NamedQuery(name = "Items.findByDescrPl", query = "SELECT i FROM Items i WHERE i.descrPl = :descrPl"),
    @NamedQuery(name = "Items.findByEmc1", query = "SELECT i FROM Items i WHERE i.emc1 = :emc1"),
    @NamedQuery(name = "Items.findByEmc2", query = "SELECT i FROM Items i WHERE i.emc2 = :emc2"),
    @NamedQuery(name = "Items.findByEmc3", query = "SELECT i FROM Items i WHERE i.emc3 = :emc3"),
    @NamedQuery(name = "Items.findByEmc4", query = "SELECT i FROM Items i WHERE i.emc4 = :emc4"),
    @NamedQuery(name = "Items.findByEmc5", query = "SELECT i FROM Items i WHERE i.emc5 = :emc5"),
    @NamedQuery(name = "Items.findByEmc6", query = "SELECT i FROM Items i WHERE i.emc6 = :emc6"),
    @NamedQuery(name = "Items.findByEmc7", query = "SELECT i FROM Items i WHERE i.emc7 = :emc7"),
    @NamedQuery(name = "Items.findByEmc8", query = "SELECT i FROM Items i WHERE i.emc8 = :emc8"),
    @NamedQuery(name = "Items.findByEmc9", query = "SELECT i FROM Items i WHERE i.emc9 = :emc9"),
    @NamedQuery(name = "Items.findByEmc10", query = "SELECT i FROM Items i WHERE i.emc10 = :emc10"),
    @NamedQuery(name = "Items.findByLvd1", query = "SELECT i FROM Items i WHERE i.lvd1 = :lvd1"),
    @NamedQuery(name = "Items.findByLvd2", query = "SELECT i FROM Items i WHERE i.lvd2 = :lvd2"),
    @NamedQuery(name = "Items.findByLvd3", query = "SELECT i FROM Items i WHERE i.lvd3 = :lvd3"),
    @NamedQuery(name = "Items.findByLvd4", query = "SELECT i FROM Items i WHERE i.lvd4 = :lvd4"),
    @NamedQuery(name = "Items.findByLvd5", query = "SELECT i FROM Items i WHERE i.lvd5 = :lvd5"),
    @NamedQuery(name = "Items.findByLvd6", query = "SELECT i FROM Items i WHERE i.lvd6 = :lvd6"),
    @NamedQuery(name = "Items.findByLvd7", query = "SELECT i FROM Items i WHERE i.lvd7 = :lvd7"),
    @NamedQuery(name = "Items.findByLvd8", query = "SELECT i FROM Items i WHERE i.lvd8 = :lvd8"),
    @NamedQuery(name = "Items.findByLvd9", query = "SELECT i FROM Items i WHERE i.lvd9 = :lvd9"),
    @NamedQuery(name = "Items.findByCpd1", query = "SELECT i FROM Items i WHERE i.cpd1 = :cpd1"),
    @NamedQuery(name = "Items.findByCpd2", query = "SELECT i FROM Items i WHERE i.cpd2 = :cpd2"),
    @NamedQuery(name = "Items.findByCpd3", query = "SELECT i FROM Items i WHERE i.cpd3 = :cpd3"),
    @NamedQuery(name = "Items.findByCpd4", query = "SELECT i FROM Items i WHERE i.cpd4 = :cpd4"),
    @NamedQuery(name = "Items.findByRf1", query = "SELECT i FROM Items i WHERE i.rf1 = :rf1"),
    @NamedQuery(name = "Items.findByRf2", query = "SELECT i FROM Items i WHERE i.rf2 = :rf2"),
    @NamedQuery(name = "Items.findByRf3", query = "SELECT i FROM Items i WHERE i.rf3 = :rf3"),
    @NamedQuery(name = "Items.findByRf4", query = "SELECT i FROM Items i WHERE i.rf4 = :rf4"),
    @NamedQuery(name = "Items.findByGs", query = "SELECT i FROM Items i WHERE i.gs = :gs"),
    @NamedQuery(name = "Items.findByGsCdf", query = "SELECT i FROM Items i WHERE i.gsCdf = :gsCdf"),
    @NamedQuery(name = "Items.findByGsCe", query = "SELECT i FROM Items i WHERE i.gsCe = :gsCe"),
    @NamedQuery(name = "Items.findByGsTr", query = "SELECT i FROM Items i WHERE i.gsTr = :gsTr"),
    @NamedQuery(name = "Items.findByGsDate", query = "SELECT i FROM Items i WHERE i.gsDate = :gsDate"),
    @NamedQuery(name = "Items.findByGsNb", query = "SELECT i FROM Items i WHERE i.gsNb = :gsNb"),
    @NamedQuery(name = "Items.findByLvd", query = "SELECT i FROM Items i WHERE i.lvd = :lvd"),
    @NamedQuery(name = "Items.findByPhotobiol", query = "SELECT i FROM Items i WHERE i.photobiol = :photobiol"),
    @NamedQuery(name = "Items.findByPhotobiolTr", query = "SELECT i FROM Items i WHERE i.photobiolTr = :photobiolTr"),
    @NamedQuery(name = "Items.findByIpclass", query = "SELECT i FROM Items i WHERE i.ipclass = :ipclass"),
    @NamedQuery(name = "Items.findByIpclassTr", query = "SELECT i FROM Items i WHERE i.ipclassTr = :ipclassTr"),
    @NamedQuery(name = "Items.findByLvdCe", query = "SELECT i FROM Items i WHERE i.lvdCe = :lvdCe"),
    @NamedQuery(name = "Items.findByLvdCert", query = "SELECT i FROM Items i WHERE i.lvdCe = :lvdCert"),
    @NamedQuery(name = "Items.findByEmcCert", query = "SELECT i FROM Items i WHERE i.lvdCe = :emcCert"),
    @NamedQuery(name = "Items.findByRfCert", query = "SELECT i FROM Items i WHERE i.lvdCe = :rfCert"),
    @NamedQuery(name = "Items.findByCpdDir", query = "SELECT i FROM Items i WHERE i.lvdCe = :cpdDir"),
    @NamedQuery(name = "Items.findByRohsCert", query = "SELECT i FROM Items i WHERE i.lvdCe = :rohsCert"),
    @NamedQuery(name = "Items.findByLvdTr", query = "SELECT i FROM Items i WHERE i.lvdTr = :lvdTr"),
    @NamedQuery(name = "Items.findByLvdDate", query = "SELECT i FROM Items i WHERE i.lvdDate = :lvdDate"),
    @NamedQuery(name = "Items.findByLvdNb", query = "SELECT i FROM Items i WHERE i.lvdNb = :lvdNb"),
    @NamedQuery(name = "Items.findByEmc", query = "SELECT i FROM Items i WHERE i.emc = :emc"),
    @NamedQuery(name = "Items.findByEmcCe", query = "SELECT i FROM Items i WHERE i.emcCe = :emcCe"),
    @NamedQuery(name = "Items.findByEmcTr", query = "SELECT i FROM Items i WHERE i.emcTr = :emcTr"),
    @NamedQuery(name = "Items.findByEmcDate", query = "SELECT i FROM Items i WHERE i.emcDate = :emcDate"),
    @NamedQuery(name = "Items.findByEmcNb", query = "SELECT i FROM Items i WHERE i.emcNb = :emcNb"),
    @NamedQuery(name = "Items.findByCpd", query = "SELECT i FROM Items i WHERE i.cpd = :cpd"),
    @NamedQuery(name = "Items.findByCpdCe", query = "SELECT i FROM Items i WHERE i.cpdCe = :cpdCe"),
    @NamedQuery(name = "Items.findByCpdTr", query = "SELECT i FROM Items i WHERE i.cpdTr = :cpdTr"),
    @NamedQuery(name = "Items.findByCpdDate", query = "SELECT i FROM Items i WHERE i.cpdDate = :cpdDate"),
    @NamedQuery(name = "Items.findByCpdNb", query = "SELECT i FROM Items i WHERE i.cpdNb = :cpdNb"),
    @NamedQuery(name = "Items.findByVds", query = "SELECT i FROM Items i WHERE i.vds = :vds"),
    @NamedQuery(name = "Items.findByVdsCe", query = "SELECT i FROM Items i WHERE i.vdsCe = :vdsCe"),
    @NamedQuery(name = "Items.findByVdsTr", query = "SELECT i FROM Items i WHERE i.vdsTr = :vdsTr"),
    @NamedQuery(name = "Items.findByVdsDate", query = "SELECT i FROM Items i WHERE i.vdsDate = :vdsDate"),
    @NamedQuery(name = "Items.findByBosec", query = "SELECT i FROM Items i WHERE i.bosec = :bosec"),
    @NamedQuery(name = "Items.findByBosecCe", query = "SELECT i FROM Items i WHERE i.bosecCe = :bosecCe"),
    @NamedQuery(name = "Items.findByBosecDate", query = "SELECT i FROM Items i WHERE i.bosecDate = :bosecDate"),
    @NamedQuery(name = "Items.findByKomo", query = "SELECT i FROM Items i WHERE i.komo = :komo"),
    @NamedQuery(name = "Items.findByKomoCe", query = "SELECT i FROM Items i WHERE i.komoCe = :komoCe"),
    @NamedQuery(name = "Items.findByKomoDate", query = "SELECT i FROM Items i WHERE i.komoDate = :komoDate"),
    @NamedQuery(name = "Items.findByNf", query = "SELECT i FROM Items i WHERE i.nf = :nf"),
    @NamedQuery(name = "Items.findByNfCe", query = "SELECT i FROM Items i WHERE i.nfCe = :nfCe"),
    @NamedQuery(name = "Items.findByNfTr", query = "SELECT i FROM Items i WHERE i.nfTr = :nfTr"),
    @NamedQuery(name = "Items.findByNfDate", query = "SELECT i FROM Items i WHERE i.nfDate = :nfDate"),
    @NamedQuery(name = "Items.findByKk", query = "SELECT i FROM Items i WHERE i.kk = :kk"),
    @NamedQuery(name = "Items.findByKkCe", query = "SELECT i FROM Items i WHERE i.kkCe = :kkCe"),
    @NamedQuery(name = "Items.findByKkDate", query = "SELECT i FROM Items i WHERE i.kkDate = :kkDate"),
    @NamedQuery(name = "Items.findByRf", query = "SELECT i FROM Items i WHERE i.rf = :rf"),
    @NamedQuery(name = "Items.findByRfCe", query = "SELECT i FROM Items i WHERE i.rfCe = :rfCe"),
    @NamedQuery(name = "Items.findByRfTr", query = "SELECT i FROM Items i WHERE i.rfTr = :rfTr"),
    @NamedQuery(name = "Items.findByRfDate", query = "SELECT i FROM Items i WHERE i.rfDate = :rfDate"),
    @NamedQuery(name = "Items.findByRfNb", query = "SELECT i FROM Items i WHERE i.rfNb = :rfNb"),
    @NamedQuery(name = "Items.findByRfNbN", query = "SELECT i FROM Items i WHERE i.rfNbN = :rfNbN"),
    @NamedQuery(name = "Items.findByRfF", query = "SELECT i FROM Items i WHERE i.rfF= :rfF"),
    @NamedQuery(name = "Items.findByRohs", query = "SELECT i FROM Items i WHERE i.rohs = :rohs"),
    @NamedQuery(name = "Items.findByRohsCe", query = "SELECT i FROM Items i WHERE i.rohsCe = :rohsCe"),
    @NamedQuery(name = "Items.findByRohsTr", query = "SELECT i FROM Items i WHERE i.rohsTr = :rohsTr"),
    @NamedQuery(name = "Items.findByRohsDate", query = "SELECT i FROM Items i WHERE i.rohsDate = :rohsDate"),
    @NamedQuery(name = "Items.findByRohsNb", query = "SELECT i FROM Items i WHERE i.rohsNb = :rohsNb"),
    @NamedQuery(name = "Items.findByKind_bulb", query = "SELECT i FROM Items i WHERE i.kind_bulb = :kind_bulb"),
    @NamedQuery(name = "Items.findByReach", query = "SELECT i FROM Items i WHERE i.reach = :reach"),
    @NamedQuery(name = "Items.findByReachCe", query = "SELECT i FROM Items i WHERE i.reachCe = :reachCe"),
    @NamedQuery(name = "Items.findByDoc", query = "SELECT i FROM Items i WHERE i.doc = :doc"),
    @NamedQuery(name = "Items.findByDoi", query = "SELECT i FROM Items i WHERE i.doi = :doi"),
    @NamedQuery(name = "Items.findByEup", query = "SELECT i FROM Items i WHERE i.eup = :eup"),
    @NamedQuery(name = "Items.findByEupCe", query = "SELECT i FROM Items i WHERE i.eupCe = :eupCe"),
    @NamedQuery(name = "Items.findByEupTr", query = "SELECT i FROM Items i WHERE i.eupTr = :eupTr"),
    @NamedQuery(name = "Items.findByEupDate", query = "SELECT i FROM Items i WHERE i.eupDate = :eupDate"),
    @NamedQuery(name = "Items.findByEupStatus", query = "SELECT i FROM Items i WHERE i.eupStatus = :eupStatus"),
    @NamedQuery(name = "Items.findByFlux", query = "SELECT i FROM Items i WHERE i.flux = :flux"),
    @NamedQuery(name = "Items.findByFluxTr", query = "SELECT i FROM Items i WHERE i.fluxTr = :fluxTr"),
    @NamedQuery(name = "Items.findByUV", query = "SELECT i FROM Items i WHERE i.uv = :uv"),
    @NamedQuery(name = "Items.findBySpectrum", query = "SELECT i FROM Items i WHERE i.spectrum = :spectrum"),
    @NamedQuery(name = "Items.findByPah", query = "SELECT i FROM Items i WHERE i.pah = :pah"),
    @NamedQuery(name = "Items.findBySpecial_use", query = "SELECT i FROM Items i WHERE i.special_use = :special_use"),
    @NamedQuery(name = "Items.findByAccent", query = "SELECT i FROM Items i WHERE i.accent = :accent"),
    @NamedQuery(name = "Items.findByBatt", query = "SELECT i FROM Items i WHERE i.batt = :batt"),
    @NamedQuery(name = "Items.findByBattm", query = "SELECT i FROM Items i WHERE i.batt = :battm"),
    @NamedQuery(name = "Items.findByBATT2", query = "SELECT i FROM Items i WHERE i.batt2 = :batt2"),
    @NamedQuery(name = "Items.findByBattTr2", query = "SELECT i FROM Items i WHERE i.battTr2 = :battTr2"),
    @NamedQuery(name = "Items.findByPhth", query = "SELECT i FROM Items i WHERE i.phth = :phth"),
    @NamedQuery(name = "Items.findByOem", query = "SELECT i FROM Items i WHERE i.oem = :oem"),
    @NamedQuery(name = "Items.findByOemCe", query = "SELECT i FROM Items i WHERE i.oemCe = :oemCe"),
    @NamedQuery(name = "Items.findByOemDateFrom", query = "SELECT i FROM Items i WHERE i.oemDateFrom = :oemDateFrom"),
    @NamedQuery(name = "Items.findByOemDateTo", query = "SELECT i FROM Items i WHERE i.oemDateTo = :oemDateTo"),
    @NamedQuery(name = "Items.findByGolden", query = "SELECT i FROM Items i WHERE i.golden = :golden"),
    @NamedQuery(name = "Items.findByGoldenT", query = "SELECT i FROM Items i WHERE i.goldenT = :goldenT"),
    @NamedQuery(name = "Items.findByModDate", query = "SELECT i FROM Items i WHERE i.modDate = :modDate"),
    @NamedQuery(name = "Items.findByModWho", query = "SELECT i FROM Items i WHERE i.modWho = :modWho"),
    @NamedQuery(name = "Items.findByValidDate", query = "SELECT i FROM Items i WHERE i.validDate = :validDate"),
    @NamedQuery(name = "Items.findByRemarks", query = "SELECT i FROM Items i WHERE i.remarks = :remarks"),
    @NamedQuery(name = "Items.findByRemarks_Auth", query = "SELECT i FROM Items i WHERE i.remarks_auth = :remarks_auth"),
    @NamedQuery(name = "Items.findByPAHCE", query = "SELECT i FROM Items i WHERE i.pahce = :pahce"),
    @NamedQuery(name = "Items.findByEan", query = "SELECT i FROM Items i WHERE i.ean = :ean"),
    @NamedQuery(name = "Items.findByIncl", query = "SELECT i FROM Items i WHERE i.incl = :incl"),
    @NamedQuery(name = "Items.findByItem_Bulb", query = "SELECT i FROM Items i WHERE i.item_bulb = :item_bulb"),
    @NamedQuery(name = "Items.findByInt_Led", query = "SELECT i FROM Items i WHERE i.int_led = :int_led"),
    @NamedQuery(name = "Items.findByWattage", query = "SELECT i FROM Items i WHERE i.wattage = :wattage"),
    @NamedQuery(name = "Items.findByWattage_Rated", query = "SELECT i FROM Items i WHERE i.wattage_rated = :wattage_rated"),
    @NamedQuery(name = "Items.findByLumen", query = "SELECT i FROM Items i WHERE i.lumen = :lumen"),
    @NamedQuery(name = "Items.findByLumen_Rated", query = "SELECT i FROM Items i WHERE i.lumen_rated = :lumen_rated"),
    @NamedQuery(name = "Items.findByLifetime", query = "SELECT i FROM Items i WHERE i.lifetime = :lifetime"),
    @NamedQuery(name = "Items.findByLifetime_Rated", query = "SELECT i FROM Items i WHERE i.lifetime_rated = :lifetime_rated"),
    @NamedQuery(name = "Items.findBySwicyc", query = "SELECT i FROM Items i WHERE i.swicyc = :swicyc"),
    @NamedQuery(name = "Items.findByKelvin", query = "SELECT i FROM Items i WHERE i.kelvin = :kelvin"),
    @NamedQuery(name = "Items.findByRa", query = "SELECT i FROM Items i WHERE i.ra = :ra"),
    @NamedQuery(name = "Items.findByStar60", query = "SELECT i FROM Items i WHERE i.star60 = :star60"),
    @NamedQuery(name = "Items.findByStart_Time", query = "SELECT i FROM Items i WHERE i.start_time = :start_time"),
    @NamedQuery(name = "Items.findByColor_Cons", query = "SELECT i FROM Items i WHERE i.color_cons = :color_cons"),
    @NamedQuery(name = "Items.findByCandela", query = "SELECT i FROM Items i WHERE i.candela = :candela"),
    @NamedQuery(name = "Items.findByBeam", query = "SELECT i FROM Items i WHERE i.beam = :beam"),
    @NamedQuery(name = "Items.findByBeam_R", query = "SELECT i FROM Items i WHERE i.beam_r = :beam_r"),
    @NamedQuery(name = "Items.findByColour", query = "SELECT i FROM Items i WHERE i.colour = :colour"),
    @NamedQuery(name = "Items.findByIndoor", query = "SELECT i FROM Items i WHERE i.indoor = :indoor"),
    @NamedQuery(name = "Items.findByDimension_L", query = "SELECT i FROM Items i WHERE i.dimension_l = :dimension_l"),
    @NamedQuery(name = "Items.findByDimension_D", query = "SELECT i FROM Items i WHERE i.dimension_d = :dimension_d"),
    @NamedQuery(name = "Items.findByDimension_Fi", query = "SELECT i FROM Items i WHERE i.dimension_fi = :dimension_fi"),
    @NamedQuery(name = "Items.findByPower_Factor", query = "SELECT i FROM Items i WHERE i.power_factor = :power_factor"),
    @NamedQuery(name = "Items.findByLumen_Factor", query = "SELECT i FROM Items i WHERE i.lumen_factor = :lumen_factor"),
    @NamedQuery(name = "Items.findByDimmer", query = "SELECT i FROM Items i WHERE i.dimmer = :dimmer"),
    @NamedQuery(name = "Items.findByEnclas", query = "SELECT i FROM Items i WHERE i.enclas = :enclas"),
    @NamedQuery(name = "Items.findByKwik", query = "SELECT i FROM Items i WHERE i.kwik = :kwik"),
    @NamedQuery(name = "Items.findByVoltage", query = "SELECT i FROM Items i WHERE i.voltage = :voltage"),
    @NamedQuery(name = "Items.findByAmpere", query = "SELECT i FROM Items i WHERE i.ampere = :ampere"),
    @NamedQuery(name = "Items.findByCompar", query = "SELECT i FROM Items i WHERE i.compar = :compar"),
    @NamedQuery(name = "Items.findByFittin", query = "SELECT i FROM Items i WHERE i.fittin = :fittin"),
    @NamedQuery(name = "Items.findByLichtb", query = "SELECT i FROM Items i WHERE i.lichtb = :lichtb"),
    @NamedQuery(name = "Items.findByShape", query = "SELECT i FROM Items i WHERE i.shape = :shape"),
    @NamedQuery(name = "Items.findByLed_Type", query = "SELECT i FROM Items i WHERE i.led_type = :led_type"),
    @NamedQuery(name = "Items.findByLed_Number", query = "SELECT i FROM Items i WHERE i.led_number = :led_number"),
    @NamedQuery(name = "Items.findByTest_Date", query = "SELECT i FROM Items i WHERE i.test_date = :test_date"),
    @NamedQuery(name = "Items.findByT6000H_Date", query = "SELECT i FROM Items i WHERE i.t6000h_date = :t6000h_date"),
    @NamedQuery(name = "Items.findByMonster", query = "SELECT i FROM Items i WHERE i.monster = :monster"),
    @NamedQuery(name = "Items.findByBuyer", query = "SELECT i FROM Items i WHERE i.buyer = :buyer"),
    @NamedQuery(name = "Items.findByContact", query = "SELECT i FROM Items i WHERE i.contact = :contact"),
    @NamedQuery(name = "Items.findByEmail", query = "SELECT i FROM Items i WHERE i.email = :email"),
    @NamedQuery(name = "Items.findByAssortment", query = "SELECT i FROM Items i WHERE i.assortment = :assortment"),
    @NamedQuery(name = "Items.findByPromotion", query = "SELECT i FROM Items i WHERE i.promotion = :promotion"),
    @NamedQuery(name = "Items.findByNewitem", query = "SELECT i FROM Items i WHERE i.newitem = :newitem"),
    @NamedQuery(name = "Items.findByCheckdate", query = "SELECT i FROM Items i WHERE i.checkdate = :checkdate"),
    @NamedQuery(name = "Items.findByUpdate", query = "SELECT i FROM Items i WHERE i.update = :update"),
    @NamedQuery(name = "Items.findByCertifcheck", query = "SELECT i FROM Items i WHERE i.certifcheck = :certifcheck"),
    @NamedQuery(name = "Items.findByTechnqccheck", query = "SELECT i FROM Items i WHERE i.technqccheck = :technqccheck"),
    @NamedQuery(name = "Items.findByFunctcheck", query = "SELECT i FROM Items i WHERE i.functcheck = :functcheck"),
    @NamedQuery(name = "Items.findByTechntdcheck", query = "SELECT i FROM Items i WHERE i.techntdcheck = :techntdcheck"),
    @NamedQuery(name = "Items.findByConclusion", query = "SELECT i FROM Items i WHERE i.conclusion = :conclusion"),
    @NamedQuery(name = "Items.findByMAINS", query = "SELECT i FROM Items i WHERE i.mains = :mains"),
    @NamedQuery(name = "Items.findByMAINS_IN_VOLT", query = "SELECT i FROM Items i WHERE i.mains_in_volt = :mains_in_volt"),
    @NamedQuery(name = "Items.findByMAINS_IN_WATT", query = "SELECT i FROM Items i WHERE i.mains_in_watt = :mains_in_watt"),
    @NamedQuery(name = "Items.findByMAINS_IN_PLUG", query = "SELECT i FROM Items i WHERE i.mains_in_plug = :mains_in_plug"),
    @NamedQuery(name = "Items.findByIP_RATE", query = "SELECT i FROM Items i WHERE i.ip_rate = :ip_rate"),
    @NamedQuery(name = "Items.findByMAINS_CLASS", query = "SELECT i FROM Items i WHERE i.mains_class = :mains_class"),
    @NamedQuery(name = "Items.findByMAINS_OUT_WATT", query = "SELECT i FROM Items i WHERE i.mains_out_watt = :mains_out_watt"),
    @NamedQuery(name = "Items.findByMAINS_OUT_PLUG", query = "SELECT i FROM Items i WHERE i.mains_out_plug = :mains_out_plug"),
    @NamedQuery(name = "Items.findByADAPTOR1", query = "SELECT i FROM Items i WHERE i.adaptor1 = :adaptor1"),
    @NamedQuery(name = "Items.findByADAPTOR2", query = "SELECT i FROM Items i WHERE i.adaptor2 = :adaptor2"),
    @NamedQuery(name = "Items.findByADAPTOR_TYPE1", query = "SELECT i FROM Items i WHERE i.adaptor_type1 = :adaptor_type1"),
    @NamedQuery(name = "Items.findByADAPTOR_TYPE2", query = "SELECT i FROM Items i WHERE i.adaptor_type2 = :adaptor_type2"),
    @NamedQuery(name = "Items.findByIN_VOLT1", query = "SELECT i FROM Items i WHERE i.in_volt1 = :in_volt1"),
    @NamedQuery(name = "Items.findByOUT_VOLT1", query = "SELECT i FROM Items i WHERE i.out_volt1 = :out_volt1"),
    @NamedQuery(name = "Items.findByIN_VOLT2", query = "SELECT i FROM Items i WHERE i.in_volt2 = :in_volt2"),
    @NamedQuery(name = "Items.findByOUT_VOLT2", query = "SELECT i FROM Items i WHERE i.out_volt2 = :out_volt2"),
    @NamedQuery(name = "Items.findByIN_AMP1", query = "SELECT i FROM Items i WHERE i.in_amp1 = :in_amp1"),
    @NamedQuery(name = "Items.findByOUT_AMP1", query = "SELECT i FROM Items i WHERE i.out_amp1 = :out_amp1"),
    @NamedQuery(name = "Items.findByIN_AMP2", query = "SELECT i FROM Items i WHERE i.in_amp2 = :in_amp2"),
    @NamedQuery(name = "Items.findByOUT_AMP2", query = "SELECT i FROM Items i WHERE i.out_amp2 = :out_amp2"),
    @NamedQuery(name = "Items.findByIN_WATT1", query = "SELECT i FROM Items i WHERE i.in_watt1 = :in_watt1"),
    @NamedQuery(name = "Items.findByOUT_WATT1", query = "SELECT i FROM Items i WHERE i.out_watt1 = :out_watt1"),
    @NamedQuery(name = "Items.findByIN_WATT2", query = "SELECT i FROM Items i WHERE i.in_watt2 = :in_watt2"),
    @NamedQuery(name = "Items.findByOUT_WATT2", query = "SELECT i FROM Items i WHERE i.out_watt2 = :out_watt2"),
    @NamedQuery(name = "Items.findByAPPLIANCE_CLASS1", query = "SELECT i FROM Items i WHERE i.appliance_class1 = :appliance_class1"),
    @NamedQuery(name = "Items.findByAPPLIANCE_CLASS2", query = "SELECT i FROM Items i WHERE i.appliance_class2 = :appliance_class2"),
    @NamedQuery(name = "Items.findByOUT_LOCK", query = "SELECT i FROM Items i WHERE i.out_lock = :out_lock"),
    @NamedQuery(name = "Items.findByBATT_QUA1", query = "SELECT i FROM Items i WHERE i.batt_qua1 = :batt_qua1"),
    @NamedQuery(name = "Items.findByBATT_BRAND1", query = "SELECT i FROM Items i WHERE i.batt_brand1 = :batt_brand1"),
    @NamedQuery(name = "Items.findByBATT_ACCU1", query = "SELECT i FROM Items i WHERE i.batt_accu1 = :batt_accu1"),
    @NamedQuery(name = "Items.findByBATT_CAP1", query = "SELECT i FROM Items i WHERE i.batt_cap1 = :batt_cap1"),
    @NamedQuery(name = "Items.findByBATT_TYPE1", query = "SELECT i FROM Items i WHERE i.batt_type1 = :batt_type1"),
    @NamedQuery(name = "Items.findByBATT_SIZE1", query = "SELECT i FROM Items i WHERE i.batt_size1 = :batt_size1"),
    @NamedQuery(name = "Items.findByBATT_VOLT1", query = "SELECT i FROM Items i WHERE i.batt_volt1 = :batt_volt1"),
    @NamedQuery(name = "Items.findByBATT_REPL1", query = "SELECT i FROM Items i WHERE i.batt_repl1 = :batt_repl1"),
    @NamedQuery(name = "Items.findByBATT_QUA2", query = "SELECT i FROM Items i WHERE i.batt_qua2 = :batt_qua2"),
    @NamedQuery(name = "Items.findByBATT_BRAND2", query = "SELECT i FROM Items i WHERE i.batt_brand2 = :batt_brand2"),
    @NamedQuery(name = "Items.findByBATT_ACCU2", query = "SELECT i FROM Items i WHERE i.batt_accu2 = :batt_accu2"),
    @NamedQuery(name = "Items.findByBATT_CAP2", query = "SELECT i FROM Items i WHERE i.batt_cap2 = :batt_cap2"),
    @NamedQuery(name = "Items.findByBATT_TYPE2", query = "SELECT i FROM Items i WHERE i.batt_type2 = :batt_type2"),
    @NamedQuery(name = "Items.findByBATT_SIZE2", query = "SELECT i FROM Items i WHERE i.batt_size2 = :batt_size2"),
    @NamedQuery(name = "Items.findByBATT_VOLT2", query = "SELECT i FROM Items i WHERE i.batt_volt2 = :batt_volt2"),
    @NamedQuery(name = "Items.findByBATT_REPL2", query = "SELECT i FROM Items i WHERE i.batt_repl2 = :batt_repl2"),
    @NamedQuery(name = "Items.findByMAX_WATT1", query = "SELECT i FROM Items i WHERE i.max_watt1 = :max_watt1"),
    @NamedQuery(name = "Items.findByTYPE_BULB1", query = "SELECT i FROM Items i WHERE i.type_bulb1 = :type_bulb1"),
    @NamedQuery(name = "Items.findByMAX_WATT2", query = "SELECT i FROM Items i WHERE i.max_watt2 = :max_watt2"),
    @NamedQuery(name = "Items.findByTYPE_BULB2", query = "SELECT i FROM Items i WHERE i.type_bulb2 = :type_bulb2"),
    @NamedQuery(name = "Items.findByFIXTURE_TYPE", query = "SELECT i FROM Items i WHERE i.fixture_type = :fixture_type"),
    @NamedQuery(name = "Items.findByFIXTURE_SIZE", query = "SELECT i FROM Items i WHERE i.fixture_size = :fixture_size"),
    @NamedQuery(name = "Items.findByBULB_CLASS", query = "SELECT i FROM Items i WHERE i.bulb_class = :bulb_class"),
    @NamedQuery(name = "Items.findBySENSOR_TYPE", query = "SELECT i FROM Items i WHERE i.sensor_type = :sensor_type"),
    @NamedQuery(name = "Items.findBySENSOR_SIZE", query = "SELECT i FROM Items i WHERE i.sensor_size = :sensor_size"),
    @NamedQuery(name = "Items.findByPIXELS", query = "SELECT i FROM Items i WHERE i.pixels = :pixels"),
    @NamedQuery(name = "Items.findByIRLED", query = "SELECT i FROM Items i WHERE i.irled = :irled"),
    @NamedQuery(name = "Items.findBySCREEN_SIZE", query = "SELECT i FROM Items i WHERE i.screen_size = :screen_size"),
    @NamedQuery(name = "Items.findByTV_LINES", query = "SELECT i FROM Items i WHERE i.tv_lines = :tv_lines"),
    @NamedQuery(name = "Items.findByBELL_SOUND", query = "SELECT i FROM Items i WHERE i.bell_sound = :bell_sound"),
    @NamedQuery(name = "Items.findByBELL_TEMP", query = "SELECT i FROM Items i WHERE i.bell_temp = :bell_temp"),
    @NamedQuery(name = "Items.findByCE", query = "SELECT i FROM Items i WHERE i.CE = :CE"),
    @NamedQuery(name = "Items.findByWEEE", query = "SELECT i FROM Items i WHERE i.WEEE = :WEEE")
})
public class Items implements Serializable {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "HIDDEN")
    private boolean hidden;
    @Basic(optional = false)
    @Column(name = "ITEM")
    private String item;
    @Column(name = "SAP")
    private String sap;
    @Column(name = "COMPONENT1")
    private String component1;
    @Column(name = "COMPONENT2")
    private String component2;
    @Column(name = "COMPONENT3")
    private String component3;
    @Column(name = "COMPONENT4")
    private String component4;
    @Column(name = "COMPONENT5")
    private String component5;
    @Column(name = "COMPONENT6")
    private String component6;
    @Column(name = "COMPONENT7")
    private String component7;
    @Column(name = "COMPONENT8")
    private String component8;
    @Column(name = "COMPONENT9")
    private String component9;
    @Column(name = "COMPONENT10")
    private String component10;
    @Column(name = "BRAND")
    private String brand;
    @Column(name = "VENDOR")
    private String vendor;
    @Column(name = "SUPPLIER")
    private String supplier;
    @Column(name = "ITEM_S")
    private String sItem;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "QC_STATUS")
    private String qc_status;
    @Column(name = "QM_STATUS")
    private String qm_status;
    @Column(name = "HIERARCHY")
    private String hierarchy;
    @Column(name = "DESCR_EN")
    private String descrEn;
    @Column(name = "DESCR_FR")
    private String descrFr;
    @Column(name = "DESCR_DE")
    private String descrDe;
    @Column(name = "DESCR_NL")
    private String descrNl;
    @Column(name = "DESCR_ES")
    private String descrEs;
    @Column(name = "DESCR_PL")
    private String descrPl;
    @Column(name = "EMC1")
    private String emc1;
    @Column(name = "EMC2")
    private String emc2;
    @Column(name = "EMC3")
    private String emc3;
    @Column(name = "EMC4")
    private String emc4;
    @Column(name = "EMC5")
    private String emc5;
    @Column(name = "EMC6")
    private String emc6;
    @Column(name = "EMC7")
    private String emc7;
    @Column(name = "EMC8")
    private String emc8;
    @Column(name = "EMC9")
    private String emc9;
    @Column(name = "EMC10")
    private String emc10;
    @Column(name = "LVD1")
    private String lvd1;
    @Column(name = "LVD2")
    private String lvd2;
    @Column(name = "LVD3")
    private String lvd3;
    @Column(name = "LVD4")
    private String lvd4;
    @Column(name = "LVD5")
    private String lvd5;
    @Column(name = "LVD6")
    private String lvd6;
    @Column(name = "LVD7")
    private String lvd7;
    @Column(name = "LVD8")
    private String lvd8;
    @Column(name = "LVD9")
    private String lvd9;
    @Column(name = "CPD1")
    private String cpd1;
    @Column(name = "CPD2")
    private String cpd2;
    @Column(name = "CPD3")
    private String cpd3;
    @Column(name = "CPD4")
    private String cpd4;
    @Column(name = "RF1")
    private String rf1;
    @Column(name = "RF2")
    private String rf2;
    @Column(name = "RF3")
    private String rf3;
    @Column(name = "RF4")
    private String rf4;
    @Basic(optional = false)
    @Column(name = "GS")
    private boolean gs;
    @Basic(optional = false)
    @Column(name = "GS_CDF")
    private boolean gsCdf;
    @Column(name = "GS_CE")
    private String gsCe;
    @Column(name = "GS_TR")
    private String gsTr;
    @Column(name = "GS_DATE")
    @Temporal(TemporalType.DATE)
    private Date gsDate;
    @Column(name = "GS_NB")
    private String gsNb;
    @Basic(optional = false)
    @Column(name = "LVD")
    private boolean lvd;
    @Basic(optional = false)
    @Column(name = "PHOTOBIOL")
    private boolean photobiol;
    @Column(name = "PHOTOBIOL_TR")
    private String photobiolTr;
    @Basic(optional = false)
    @Column(name = "IPCLASS")
    private boolean ipclass;
    @Column(name = "IPCLASS_TR")
    private String ipclassTr;
    @Column(name = "LVD_CE")
    private String lvdCe;
    @Column(name = "LVD_CERT")
    private String lvdCert;
    @Column(name = "EMC_CERT")
    private String emcCert;
    @Column(name = "RF_CERT")
    private String rfCert;
    @Column(name = "CPD_DIR")
    private String cpdDir;
    @Column(name = "ROHS_CERT")
    private String rohsCert;
    @Column(name = "LVD_TR")
    private String lvdTr;
    @Column(name = "LVD_DATE")
    @Temporal(TemporalType.DATE)
    private Date lvdDate;
    @Column(name = "LVD_NB")
    private String lvdNb;
    @Basic(optional = false)
    @Column(name = "EMC")
    private boolean emc;
    @Column(name = "EMC_CE")
    private String emcCe;
    @Column(name = "EMC_TR")
    private String emcTr;
    @Column(name = "EMC_DATE")
    @Temporal(TemporalType.DATE)
    private Date emcDate;
    @Column(name = "EMC_NB")
    private String emcNb;
    @Basic(optional = false)
    @Column(name = "CPD")
    private boolean cpd;
    @Column(name = "CPD_CE")
    private String cpdCe;
    @Column(name = "CPD_TR")
    private String cpdTr;
    @Column(name = "CPD_DATE")
    @Temporal(TemporalType.DATE)
    private Date cpdDate;
    @Column(name = "CPD_NB")
    private String cpdNb;
    @Basic(optional = false)
    @Column(name = "VDS")
    private boolean vds;
    @Column(name = "VDS_CE")
    private String vdsCe;
    @Column(name = "VDS_TR")
    private String vdsTr;
    @Column(name = "VDS_DATE")
    @Temporal(TemporalType.DATE)
    private Date vdsDate;
    @Basic(optional = false)
    @Column(name = "BOSEC")
    private boolean bosec;
    @Column(name = "BOSEC_CE")
    private String bosecCe;
    @Column(name = "BOSEC_DATE")
    @Temporal(TemporalType.DATE)
    private Date bosecDate;
    @Basic(optional = false)
    @Column(name = "KOMO")
    private boolean komo;
    @Column(name = "KOMO_CE")
    private String komoCe;
    @Column(name = "KOMO_DATE")
    @Temporal(TemporalType.DATE)
    private Date komoDate;
    @Basic(optional = false)
    @Column(name = "NF")
    private boolean nf;
    @Column(name = "NF_CE")
    private String nfCe;
    @Column(name = "NF_TR")
    private String nfTr;
    @Column(name = "NF_DATE")
    @Temporal(TemporalType.DATE)
    private Date nfDate;
    @Basic(optional = false)
    @Column(name = "KK")
    private boolean kk;
    @Column(name = "KK_CE")
    private String kkCe;
    @Column(name = "KK_DATE")
    @Temporal(TemporalType.DATE)
    private Date kkDate;
    @Basic(optional = false)
    @Column(name = "GOLDEN")
    private boolean golden;
    @Column(name = "GOLDEN_T")
    private String goldenT;
    @Basic(optional = false)
    @Column(name = "RF")
    private boolean rf;
    @Column(name = "RF_CE")
    private String rfCe;
    @Column(name = "RF_TR")
    private String rfTr;
    @Column(name = "RF_DATE")
    @Temporal(TemporalType.DATE)
    private Date rfDate;
    @Column(name = "RF_NB")
    private String rfNb;
    @Column(name = "RF_NBN")
    private String rfNbN;
    @Column(name = "RF_F")
    private String rfF;
    @Basic(optional = false)
    @Column(name = "ROHS")
    private boolean rohs;
    @Column(name = "ROHS_CE")
    private String rohsCe;
    @Column(name = "ROHS_TR")
    private String rohsTr;
    @Column(name = "ROHS_DATE")
    @Temporal(TemporalType.DATE)
    private Date rohsDate;
    @Column(name = "ROHS_NB")
    private String rohsNb;
    @Column(name = "KIND_BULB")
    private String kind_bulb;
    @Basic(optional = false)
    @Column(name = "REACH")
    private boolean reach;
    @Column(name = "REACH_CE")
    private String reachCe;
    @Basic(optional = false)
    @Column(name = "DOC")
    private boolean doc;
    @Basic(optional = false)
    @Column(name = "DOI")
    private boolean doi;
    @Basic(optional = false)
    @Column(name = "EUP")
    private boolean eup;
    @Column(name = "EUP_CE")
    private String eupCe;
    @Column(name = "EUP_TR")
    private String eupTr;
    @Column(name = "EUP_DATE")
    @Temporal(TemporalType.DATE)
    private Date eupDate;
    @Column(name = "EUP_STATUS")
    private String eupStatus;
    @Basic(optional = false)
    @Column(name = "FLUX")
    private boolean flux;
    @Column(name = "FLUX_TR")
    private String fluxTr;
    @Basic(optional = false)
    @Column(name = "UV")
    private boolean uv;
    @Column(name = "SPECTRUM")
    private String spectrum;
    @Basic(optional = false)
    @Column(name = "PAH")
    private boolean pah;
    @Column(name = "SPECIAL_USE")
    private String special_use;
    @Basic(optional = false)
    @Column(name = "ACCENT")
    private boolean accent;
    @Basic(optional = false)
    @Column(name = "BATT")
    private boolean batt;
    @Column(name = "BATT_M")
    private String battm;
    @Basic(optional = false)
    @Column(name = "BATT2")
    private boolean batt2;
    @Column(name = "BATT_TR2")
    private String battTr2;
    @Basic(optional = false)
    @Column(name = "PHTH")
    private boolean phth;
    @Basic(optional = false)
    @Column(name = "OEM")
    private boolean oem;
    @Column(name = "OEM_CE")
    private String oemCe;
    @Column(name = "OEM_DATE_FROM")
    @Temporal(TemporalType.DATE)
    private Date oemDateFrom;
    @Column(name = "OEM_DATE_TO")
    @Temporal(TemporalType.DATE)
    private Date oemDateTo;
    @Column(name = "MOD_DATE")
    private String modDate;
    @Column(name = "MOD_WHO")
    private String modWho;
    @Column(name = "VALID_DATE")
    private String validDate;
    @Column(name = "REMARKS")
    private String remarks;
    @Column(name = "REMARKS_AUTH")
    private String remarks_auth;
    @Column(name = "PAH_CE")
    private String pahce;
    @Column(name = "EAN")
    private String ean;
    @Basic(optional = false)
    @Column(name = "INCL")
    private boolean incl;
    @Column(name = "ITEM_BULB")
    private String item_bulb;
    @Basic(optional = false)
    @Column(name = "INT_LED")
    private boolean int_led;
    @Column(name = "WATTAGE")
    private String wattage;
    @Column(name = "WATTAGE_RATED")
    private String wattage_rated;
    @Column(name = "LUMEN")
    private String lumen;
    @Column(name = "LUMEN_RATED")
    private String lumen_rated;
    @Column(name = "LIFETIME")
    private String lifetime;
    @Column(name = "LIFETIME_RATED")
    private String lifetime_rated;
    @Column(name = "SWICYC")
    private String swicyc;
    @Column(name = "KELVIN")
    private String kelvin;
    @Column(name = "RA")
    private String ra;
    @Column(name = "STAR60")
    private String star60;
    @Column(name = "START_TIME")
    private String start_time;
    @Column(name = "COLOR_CONS")
    private String color_cons;
    @Column(name = "CANDELA")
    private String candela;
    @Column(name = "BEAM")
    private String beam;
    @Column(name = "BEAM_R")
    private String beam_r;
    @Column(name = "COLOUR")
    private String colour;
    @Column(name = "INDOOR")
    private String indoor;
    @Column(name = "DIMENSION_FI")
    private String dimension_fi;
    @Column(name = "DIMENSION_L")
    private String dimension_l;
    @Column(name = "DIMENSION_D")
    private String dimension_d;
    @Column(name = "POWER_FACTOR")
    private String power_factor;
    @Column(name = "LUMEN_FACTOR")
    private String lumen_factor;
    @Column(name = "DIMMER")
    private String dimmer;
    @Column(name = "ENCLAS")
    private String enclas;
    @Column(name = "KWIK")
    private String kwik;
    @Column(name = "VOLTAGE")
    private String voltage;
    @Column(name = "AMPERE")
    private String ampere;
    @Column(name = "COMPAR")
    private String compar;
    @Column(name = "FITTIN")
    private String fittin;
    @Column(name = "LICHTB")
    private String lichtb;
    @Column(name = "SHAPE")
    private String shape;
    @Column(name = "LED_TYPE")
    private String led_type;
    @Column(name = "LED_NUMBER")
    private String led_number;
    @Column(name = "TEST_DATE")
    @Temporal(TemporalType.DATE)
    private Date test_date;
    @Column(name = "T6000H_DATE")
    @Temporal(TemporalType.DATE)
    private Date t6000h_date;
    @Column(name = "MONSTER")
    private String monster;
    @Column(name = "BUYER")
    private String buyer;
    @Column(name = "CONTACT")
    private String contact;
    @Column(name = "EMAIL")
    private String email;
    @Basic(optional = false)
    @Column(name = "ASSORTMENT")
    private boolean assortment;
    @Basic(optional = false)
    @Column(name = "PROMOTION")
    private boolean promotion;
    @Basic(optional = false)
    @Column(name = "NEWITEM")
    private boolean newitem;
    @Column(name = "CHECK_DATE")
    @Temporal(TemporalType.DATE)
    private Date checkdate;
    @Column(name = "UPDATE_DATE")
    @Temporal(TemporalType.DATE)
    private Date update;
    @Column(name = "CERTIF_CHECK")
    @Basic(optional = false)
    private boolean certifcheck;
    @Column(name = "TECHNQC_CHECK")
    @Basic(optional = false)
    private boolean technqccheck;
    @Column(name = "FUNCT_CHECK")
    @Basic(optional = false)
    private boolean functcheck;
    @Column(name = "TECHNTD_CHECK")
    @Basic(optional = false)
    private boolean techntdcheck;
    @Column(name = "CONCLUSION")
    private String conclusion;
    @Column(name = "MAX_WATT1")
    private String max_watt1;
    @Column(name = "TYPE_BULB1")
    private String type_bulb1;
    @Column(name = "MAX_WATT2")
    private String max_watt2;
    @Column(name = "TYPE_BULB2")
    private String type_bulb2;
    @Column(name = "FIXTURE_TYPE")
    private String fixture_type;
    @Column(name = "APPLIANCE_CLASS1")
    private String appliance_class1;
    @Column(name = "APPLIANCE_CLASS2")
    private String appliance_class2;
    @Column(name = "FIXTURE_SIZE")
    private String fixture_size;
    @Column(name = "BULB_CLASS")
    private String bulb_class;
    @Basic(optional = false)
    @Column(name = "MAINS")
    private boolean mains;
    @Column(name = "MAINS_IN_VOLT")
    private String mains_in_volt;
    @Column(name = "MAINS_IN_WATT")
    private String mains_in_watt;
    @Column(name = "MAINS_IN_PLUG")
    private String mains_in_plug;
    @Column(name = "IP_RATE")
    private String ip_rate;
    @Column(name = "MAINS_CLASS")
    private String mains_class;
    @Column(name = "MAINS_OUT_WATT")
    private String mains_out_watt;
    @Column(name = "MAINS_OUT_PLUG")
    private String mains_out_plug;
    @Basic(optional = false)
    @Column(name = "ADAPTOR1")
    private boolean adaptor1;
    @Basic(optional = false)
    @Column(name = "ADAPTOR2")
    private boolean adaptor2;
    @Column(name = "ADAPTOR_TYPE1")
    private String adaptor_type1;
    @Column(name = "ADAPTOR_TYPE2")
    private String adaptor_type2;
    @Column(name = "IN_VOLT1")
    private String in_volt1;
    @Column(name = "OUT_VOLT1")
    private String out_volt1;
    @Column(name = "IN_VOLT2")
    private String in_volt2;
    @Column(name = "OUT_VOLT2")
    private String out_volt2;
    @Column(name = "IN_AMP1")
    private String in_amp1;
    @Column(name = "OUT_AMP1")
    private String out_amp1;
    @Column(name = "IN_AMP2")
    private String in_amp2;
    @Column(name = "OUT_AMP2")
    private String out_amp2;
    @Column(name = "IN_WATT1")
    private String in_watt1;
    @Column(name = "OUT_WATT1")
    private String out_watt1;
    @Column(name = "IN_WATT2")
    private String in_watt2;
    @Column(name = "OUT_WATT2")
    private String out_watt2;
    @Basic(optional = false)
    @Column(name = "OUT_LOCK")
    private boolean out_lock;
    @Column(name = "BATT_QUA1")
    private String batt_qua1;
    @Column(name = "BATT_BRAND1")
    private String batt_brand1;
    @Basic(optional = false)
    @Column(name = "BATT_ACCU1")
    private boolean batt_accu1;
    @Column(name = "BATT_CAP1")
    private String batt_cap1;
    @Column(name = "BATT_TYPE1")
    private String batt_type1;
    @Column(name = "BATT_SIZE1")
    private String batt_size1;
    @Column(name = "BATT_VOLT1")
    private String batt_volt1;
    @Basic(optional = false)
    @Column(name = "BATT_REPL1")
    private boolean batt_repl1;
    @Column(name = "BATT_QUA2")
    private String batt_qua2;
    @Column(name = "BATT_BRAND2")
    private String batt_brand2;
    @Basic(optional = false)
    @Column(name = "BATT_ACCU2")
    private boolean batt_accu2;
    @Column(name = "BATT_CAP2")
    private String batt_cap2;
    @Column(name = "BATT_TYPE2")
    private String batt_type2;
    @Column(name = "BATT_SIZE2")
    private String batt_size2;
    @Column(name = "BATT_VOLT2")
    private String batt_volt2;
    @Basic(optional = false)
    @Column(name = "BATT_REPL2")
    private boolean batt_repl2;
    @Column(name = "SENSOR_TYPE")
    private String sensor_type;
    @Column(name = "SENSOR_SIZE")
    private String sensor_size;
    @Column(name = "PIXELS")
    private String pixels;
    @Column(name = "IRLED")
    private String irled;
    @Column(name = "SCREEN_SIZE")
    private String screen_size;
    @Column(name = "TV_LINES")
    private String tv_lines;
    @Column(name = "BELL_SOUND")
    private String bell_sound;
    @Column(name = "BELL_TEMP")
    private String bell_temp;
    @Basic(optional = true)
    @Column(name = "CE")
    private boolean CE;
    @Basic(optional = true)
    @Column(name = "WEEE")
    private boolean WEEE;

    public Items() {
    }

    public Items(Integer id) {
        this.id = id;
    }

    public Items(Integer id, boolean hidden, String item, boolean gs, boolean gsCdf, boolean lvd, boolean photobiol, boolean ipclass, boolean emc, boolean cpd, boolean rf, boolean rohs,
            boolean flux, boolean reach, boolean doc, boolean doi, boolean eup, boolean uv, boolean pah, boolean accent,
            boolean batt, boolean phth, boolean oem, boolean vds, boolean bosec, boolean komo, boolean nf,
            boolean kk, boolean golden, boolean incl, boolean incl_led, boolean assortment,
            boolean promotion, boolean newitem, boolean certifcheck, boolean technqccheck, boolean functcheck, boolean techntdcheck, boolean mains,
            boolean adaptor1, boolean adaptor2, boolean out_lock, boolean batt_accu1, boolean batt_repl1, boolean batt2, boolean batt_accu2, boolean batt_repl2, boolean CE, boolean WEEE) {
        this.id = id;
        this.hidden = hidden;
        this.item = item;
        this.gs = gs;
        this.gsCdf = gsCdf;
        this.lvd = lvd;
        this.photobiol = photobiol;
        this.ipclass = ipclass;
        this.emc = emc;
        this.cpd = cpd;
        this.rf = rf;
        this.rohs = rohs;
        this.flux = flux;
        this.reach = reach;
        this.doc = doc;
        this.doi = doi;
        this.eup = eup;
        this.uv = uv;
        this.pah = pah;
        this.accent = accent;
        this.batt = batt;
        this.phth = phth;
        this.oem = oem;
        this.vds = vds;
        this.bosec = bosec;
        this.komo = komo;
        this.nf = nf;
        this.kk = kk;
        this.golden = golden;
        this.incl = incl;
        this.int_led = int_led;
        this.assortment = assortment;
        this.promotion = promotion;
        this.newitem = newitem;
        this.certifcheck = certifcheck;
        this.technqccheck = technqccheck;
        this.functcheck = functcheck;
        this.techntdcheck = techntdcheck;
        this.mains = mains;
        this.adaptor1 = adaptor1;
        this.adaptor2 = adaptor2;
        this.out_lock = out_lock;
        this.batt_accu1 = batt_accu1;
        this.batt_repl1 = batt_repl1;
        this.batt2 = batt2;
        this.batt_accu2 = batt_accu2;
        this.batt_repl2 = batt_repl2;
        this.CE = CE;
        this.WEEE = WEEE;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        Integer oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        boolean oldHidden = this.hidden;
        this.hidden = hidden;
        changeSupport.firePropertyChange("hidden", oldHidden, hidden);
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        String oldItem = this.item;
        this.item = item;
        changeSupport.firePropertyChange("item", oldItem, item);
    }

    public String getSap() {
        return sap;
    }

    public void setSap(String sap) {
        String oldSap = this.sap;
        this.sap = sap;
        changeSupport.firePropertyChange("sap", oldSap, sap);
    }

    public String getComponent1() {
        return component1;
    }

    public void setComponent1(String component1) {
        String oldComponent1 = this.component1;
        this.component1 = component1;
        changeSupport.firePropertyChange("component1", oldComponent1, component1);
    }

    public String getComponent2() {
        return component2;
    }

    public void setComponent2(String component2) {
        String oldComponent2 = this.component2;
        this.component2 = component2;
        changeSupport.firePropertyChange("component2", oldComponent2, component2);
    }

    public String getComponent3() {
        return component3;
    }

    public void setComponent3(String component3) {
        String oldComponent3 = this.component3;
        this.component3 = component3;
        changeSupport.firePropertyChange("component3", oldComponent3, component3);
    }

    public String getComponent4() {
        return component4;
    }

    public void setComponent4(String component4) {
        String oldComponent4 = this.component4;
        this.component4 = component4;
        changeSupport.firePropertyChange("component4", oldComponent4, component4);
    }

    public String getComponent5() {
        return component5;
    }

    public void setComponent5(String component5) {
        String oldComponent5 = this.component5;
        this.component5 = component5;
        changeSupport.firePropertyChange("component5", oldComponent5, component5);
    }

    public String getComponent6() {
        return component6;
    }

    public void setComponent6(String component6) {
        String oldComponent6 = this.component6;
        this.component6 = component6;
        changeSupport.firePropertyChange("component6", oldComponent6, component6);
    }

    public String getComponent7() {
        return component7;
    }

    public void setComponent7(String component7) {
        String oldComponent7 = this.component7;
        this.component7 = component7;
        changeSupport.firePropertyChange("component7", oldComponent7, component7);
    }

    public String getComponent8() {
        return component8;
    }

    public void setComponent8(String component8) {
        String oldComponent8 = this.component8;
        this.component8 = component8;
        changeSupport.firePropertyChange("component8", oldComponent8, component8);
    }

    public String getComponent9() {
        return component9;
    }

    public void setComponent9(String component9) {
        String oldComponent9 = this.component9;
        this.component9 = component9;
        changeSupport.firePropertyChange("component9", oldComponent9, component9);
    }

    public String getComponent10() {
        return component10;
    }

    public void setComponent10(String component10) {
        String oldComponent10 = this.component10;
        this.component10 = component10;
        changeSupport.firePropertyChange("component10", oldComponent10, component10);
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        String oldBrand = this.brand;
        this.brand = brand;
        changeSupport.firePropertyChange("brand", oldBrand, brand);
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        String oldVendor = this.vendor;
        this.vendor = vendor;
        changeSupport.firePropertyChange("vendor", oldVendor, vendor);
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        String oldSupplier = this.supplier;
        this.supplier = supplier;
        changeSupport.firePropertyChange("supplier", oldSupplier, supplier);
    }

    public String getsItem() {
        return sItem;
    }

    public void setsItem(String sItem) {
        String oldsItem = this.sItem;
        this.sItem = sItem;
        changeSupport.firePropertyChange("sItem", oldsItem, sItem);
    }

    public String getDescrEn() {
        return descrEn;
    }

    public void setDescrEn(String descrEn) {
        String oldDescrEn = this.descrEn;
        this.descrEn = descrEn;
        changeSupport.firePropertyChange("descrEn", oldDescrEn, descrEn);
    }

    public String getDescrFr() {
        return descrFr;
    }

    public void setDescrFr(String descrFr) {
        String oldDescrFr = this.descrFr;
        this.descrFr = descrFr;
        changeSupport.firePropertyChange("descrFr", oldDescrFr, descrFr);
    }

    public String getDescrDe() {
        return descrDe;
    }

    public void setDescrDe(String descrDe) {
        String oldDescrDe = this.descrDe;
        this.descrDe = descrDe;
        changeSupport.firePropertyChange("descrDe", oldDescrDe, descrDe);
    }

    public String getDescrNl() {
        return descrNl;
    }

    public void setDescrNl(String descrNl) {
        String oldDescrNl = this.descrNl;
        this.descrNl = descrNl;
        changeSupport.firePropertyChange("descrNl", oldDescrNl, descrNl);
    }

    public String getDescrEs() {
        return descrEs;
    }

    public void setDescrEs(String descrEs) {
        String oldDescrEs = this.descrEs;
        this.descrEs = descrEs;
        changeSupport.firePropertyChange("descrEs", oldDescrEs, descrEs);
    }

    public String getDescrPl() {
        return descrPl;
    }

    public void setDescrPl(String descrPl) {
        String oldDescrPl = this.descrPl;
        this.descrPl = descrPl;
        changeSupport.firePropertyChange("descrPl", oldDescrPl, descrPl);
    }

    public String getEmc1() {
        return emc1;
    }

    public void setEmc1(String emc1) {
        String oldEmc1 = this.emc1;
        this.emc1 = emc1;
        changeSupport.firePropertyChange("emc1", oldEmc1, emc1);
    }

    public String getEmc2() {
        return emc2;
    }

    public void setEmc2(String emc2) {
        String oldEmc2 = this.emc2;
        this.emc2 = emc2;
        changeSupport.firePropertyChange("emc2", oldEmc2, emc2);
    }

    public String getEmc3() {
        return emc3;
    }

    public void setEmc3(String emc3) {
        String oldEmc3 = this.emc3;
        this.emc3 = emc3;
        changeSupport.firePropertyChange("emc3", oldEmc3, emc3);
    }

    public String getEmc4() {
        return emc4;
    }

    public void setEmc4(String emc4) {
        String oldEmc4 = this.emc4;
        this.emc4 = emc4;
        changeSupport.firePropertyChange("emc4", oldEmc4, emc4);
    }

    public String getEmc5() {
        return emc5;
    }

    public void setEmc5(String emc5) {
        String oldEmc5 = this.emc5;
        this.emc5 = emc5;
        changeSupport.firePropertyChange("emc5", oldEmc5, emc5);
    }

    public String getEmc6() {
        return emc6;
    }

    public void setEmc6(String emc6) {
        String oldEmc6 = this.emc6;
        this.emc6 = emc6;
        changeSupport.firePropertyChange("emc6", oldEmc6, emc6);
    }

    public String getEmc7() {
        return emc7;
    }

    public void setEmc7(String emc7) {
        String oldEmc7 = this.emc7;
        this.emc7 = emc7;
        changeSupport.firePropertyChange("emc7", oldEmc7, emc7);
    }

    public String getEmc8() {
        return emc8;
    }

    public void setEmc8(String emc8) {
        String oldEmc8 = this.emc8;
        this.emc8 = emc8;
        changeSupport.firePropertyChange("emc8", oldEmc8, emc8);
    }

    public String getEmc9() {
        return emc9;
    }

    public void setEmc9(String emc9) {
        String oldEmc9 = this.emc9;
        this.emc9 = emc9;
        changeSupport.firePropertyChange("emc9", oldEmc9, emc9);
    }

    public String getEmc10() {
        return emc10;
    }

    public void setEmc10(String emc10) {
        String oldEmc10 = this.emc10;
        this.emc10 = emc10;
        changeSupport.firePropertyChange("emc10", oldEmc10, emc10);
    }

    public String getLvd1() {
        return lvd1;
    }

    public void setLvd1(String lvd1) {
        String oldLvd1 = this.lvd1;
        this.lvd1 = lvd1;
        changeSupport.firePropertyChange("lvd1", oldLvd1, lvd1);
    }

    public String getLvd2() {
        return lvd2;
    }

    public void setLvd2(String lvd2) {
        String oldLvd2 = this.lvd2;
        this.lvd2 = lvd2;
        changeSupport.firePropertyChange("lvd2", oldLvd2, lvd2);
    }

    public String getLvd3() {
        return lvd3;
    }

    public void setLvd3(String lvd3) {
        String oldLvd3 = this.lvd3;
        this.lvd3 = lvd3;
        changeSupport.firePropertyChange("lvd3", oldLvd3, lvd3);
    }

    public String getLvd4() {
        return lvd4;
    }

    public void setLvd4(String lvd4) {
        String oldLvd4 = this.lvd4;
        this.lvd4 = lvd4;
        changeSupport.firePropertyChange("lvd4", oldLvd4, lvd4);
    }

    public String getLvd5() {
        return lvd5;
    }

    public void setLvd5(String lvd5) {
        String oldLvd5 = this.lvd5;
        this.lvd5 = lvd5;
        changeSupport.firePropertyChange("lvd5", oldLvd5, lvd5);
    }

    public String getLvd6() {
        return lvd6;
    }

    public void setLvd6(String lvd6) {
        String oldLvd6 = this.lvd6;
        this.lvd6 = lvd6;
        changeSupport.firePropertyChange("lvd6", oldLvd6, lvd6);
    }

    public String getLvd7() {
        return lvd7;
    }

    public void setLvd7(String lvd7) {
        String oldLvd7 = this.lvd7;
        this.lvd7 = lvd7;
        changeSupport.firePropertyChange("lvd7", oldLvd7, lvd7);
    }

    public String getLvd8() {
        return lvd8;
    }

    public void setLvd8(String lvd8) {
        String oldLvd8 = this.lvd8;
        this.lvd8 = lvd8;
        changeSupport.firePropertyChange("lvd8", oldLvd8, lvd8);
    }

    public String getLvd9() {
        return lvd9;
    }

    public void setLvd9(String lvd9) {
        String oldLvd9 = this.lvd9;
        this.lvd9 = lvd9;
        changeSupport.firePropertyChange("lvd9", oldLvd9, lvd9);
    }

    public String getCpd1() {
        return cpd1;
    }

    public void setCpd1(String cpd1) {
        String oldCpd1 = this.cpd1;
        this.cpd1 = cpd1;
        changeSupport.firePropertyChange("cpd1", oldCpd1, cpd1);
    }

    public String getCpd2() {
        return cpd2;
    }

    public void setCpd2(String cpd2) {
        String oldCpd2 = this.cpd2;
        this.cpd2 = cpd2;
        changeSupport.firePropertyChange("cpd2", oldCpd2, cpd2);
    }

    public String getCpd3() {
        return cpd3;
    }

    public void setCpd3(String cpd3) {
        String oldCpd3 = this.cpd3;
        this.cpd3 = cpd3;
        changeSupport.firePropertyChange("cpd3", oldCpd3, cpd3);
    }

    public String getCpd4() {
        return cpd4;
    }

    public void setCpd4(String cpd4) {
        String oldCpd4 = this.cpd4;
        this.cpd4 = cpd4;
        changeSupport.firePropertyChange("cpd4", oldCpd4, cpd4);
    }

    public String getRf1() {
        return rf1;
    }

    public void setRf1(String rf1) {
        String oldRf1 = this.rf1;
        this.rf1 = rf1;
        changeSupport.firePropertyChange("rf1", oldRf1, rf1);
    }

    public String getRf2() {
        return rf2;
    }

    public void setRf2(String rf2) {
        String oldRf2 = this.rf2;
        this.rf2 = rf2;
        changeSupport.firePropertyChange("rf2", oldRf2, rf2);
    }

    public String getRf3() {
        return rf3;
    }

    public void setRf3(String rf3) {
        String oldRf3 = this.rf3;
        this.rf3 = rf3;
        changeSupport.firePropertyChange("rf3", oldRf3, rf3);
    }

    public String getRf4() {
        return rf4;
    }

    public void setRf4(String rf4) {
        String oldRf4 = this.rf4;
        this.rf4 = rf4;
        changeSupport.firePropertyChange("rf4", oldRf4, rf4);
    }

    public boolean getGs() {
        return gs;
    }

    public void setGs(boolean gs) {
        boolean oldGs = this.gs;
        this.gs = gs;
        changeSupport.firePropertyChange("gs", oldGs, gs);
    }

    public boolean getGsCdf() {
        return gsCdf;
    }

    public void setGsCdf(boolean gsCdf) {
        boolean oldGsCdf = this.gsCdf;
        this.gsCdf = gsCdf;
        changeSupport.firePropertyChange("gsCdf", oldGsCdf, gsCdf);
    }

    public String getGsCe() {
        return gsCe;
    }

    public void setGsCe(String gsCe) {
        String oldGsCe = this.gsCe;
        this.gsCe = gsCe;
        changeSupport.firePropertyChange("gsCe", oldGsCe, gsCe);
    }

    public String getGsTr() {
        return gsTr;
    }

    public void setGsTr(String gsTr) {
        String oldGsTr = this.gsTr;
        this.gsTr = gsTr;
        changeSupport.firePropertyChange("gsTr", oldGsTr, gsTr);
    }

    public Date getGsDate() {
        return gsDate;
    }

    public void setGsDate(Date gsDate) {
        Date oldGsDate = this.gsDate;
        this.gsDate = gsDate;
        changeSupport.firePropertyChange("gsDate", oldGsDate, gsDate);
    }

    public String getGsNb() {
        return gsNb;
    }

    public void setGsNb(String gsNb) {
        String oldGsNb = this.gsNb;
        this.gsNb = gsNb;
        changeSupport.firePropertyChange("gsNb", oldGsNb, gsNb);
    }

    public boolean getLvd() {
        return lvd;
    }

    public void setLvd(boolean lvd) {
        boolean oldLvd = this.lvd;
        this.lvd = lvd;
        changeSupport.firePropertyChange("lvd", oldLvd, lvd);
    }

    public boolean getPhotobiol() {
        return photobiol;
    }

    public void setPhotobiol(boolean photobiol) {
        boolean oldphotobiol = this.photobiol;
        this.photobiol = photobiol;
        changeSupport.firePropertyChange("photobiol", oldphotobiol, photobiol);
    }

    public String getPhotobiolTr() {
        return photobiolTr;
    }

    public void setPhotobiolTr(String photobiolTr) {
        String oldphotobiolTr = this.photobiolTr;
        this.photobiolTr = photobiolTr;
        changeSupport.firePropertyChange("photobiolTr", oldphotobiolTr, photobiolTr);
    }

    public boolean getIpclass() {
        return ipclass;
    }

    public void setIpclass(boolean ipclass) {
        boolean oldipclass = this.ipclass;
        this.ipclass = ipclass;
        changeSupport.firePropertyChange("ipclass", oldipclass, ipclass);
    }

    public String getIpclassTr() {
        return ipclassTr;
    }

    public void setIpclassTr(String ipclassTr) {
        String oldipclassTr = this.ipclassTr;
        this.ipclassTr = ipclassTr;
        changeSupport.firePropertyChange("ipclassTr", oldipclassTr, ipclassTr);
    }

    public String getLvdCe() {
        return lvdCe;
    }

    public void setLvdCe(String lvdCe) {
        String oldLvdCe = this.lvdCe;
        this.lvdCe = lvdCe;
        changeSupport.firePropertyChange("lvdCe", oldLvdCe, lvdCe);
    }

    public String getLvdCert() {
        return lvdCert;
    }

    public void setLvdCert(String lvdCert) {
        String oldLvdCert = this.lvdCert;
        this.lvdCert = lvdCert;
        changeSupport.firePropertyChange("lvdCert", oldLvdCert, lvdCert);
    }

    public String getLvdTr() {
        return lvdTr;
    }

    public void setLvdTr(String lvdTr) {
        String oldLvdTr = this.lvdTr;
        this.lvdTr = lvdTr;
        changeSupport.firePropertyChange("lvdTr", oldLvdTr, lvdTr);
    }

    public Date getLvdDate() {
        return lvdDate;
    }

    public void setLvdDate(Date lvdDate) {
        Date oldLvdDate = this.lvdDate;
        this.lvdDate = lvdDate;
        changeSupport.firePropertyChange("lvdDate", oldLvdDate, lvdDate);
    }

    public String getLvdNb() {
        return lvdNb;
    }

    public void setLvdNb(String lvdNb) {
        String oldLvdNb = this.lvdNb;
        this.lvdNb = lvdNb;
        changeSupport.firePropertyChange("lvdNb", oldLvdNb, lvdNb);
    }

    public boolean getEmc() {
        return emc;
    }

    public void setEmc(boolean emc) {
        boolean oldEmc = this.emc;
        this.emc = emc;
        changeSupport.firePropertyChange("emc", oldEmc, emc);
    }

    public String getEmcCe() {
        return emcCe;
    }

    public void setEmcCe(String emcCe) {
        String oldEmcCe = this.emcCe;
        this.emcCe = emcCe;
        changeSupport.firePropertyChange("emcCe", oldEmcCe, emcCe);
    }

    public String getEmcCert() {
        return emcCert;
    }

    public void setEmcCert(String emcCert) {
        String oldEmcCert = this.emcCert;
        this.emcCert = emcCert;
        changeSupport.firePropertyChange("emcCert", oldEmcCert, emcCert);
    }

    public String getEmcTr() {
        return emcTr;
    }

    public void setEmcTr(String emcTr) {
        String oldEmcTr = this.emcTr;
        this.emcTr = emcTr;
        changeSupport.firePropertyChange("emcTr", oldEmcTr, emcTr);
    }

    public Date getEmcDate() {
        return emcDate;
    }

    public void setEmcDate(Date emcDate) {
        Date oldEmcDate = this.emcDate;
        this.emcDate = emcDate;
        changeSupport.firePropertyChange("emcDate", oldEmcDate, emcDate);
    }

    public String getEmcNb() {
        return emcNb;
    }

    public void setEmcNb(String emcNb) {
        String oldEmcNb = this.emcNb;
        this.emcNb = emcNb;
        changeSupport.firePropertyChange("emcNb", oldEmcNb, emcNb);
    }

    public boolean getCpd() {
        return cpd;
    }

    public void setCpd(boolean cpd) {
        boolean oldCpd = this.cpd;
        this.cpd = cpd;
        changeSupport.firePropertyChange("cpd", oldCpd, cpd);
    }

    public String getCpdDir() {
        return cpdDir;
    }

    public void setCpdDir(String cpdDir) {
        String oldCpdDir = this.cpdDir;
        this.cpdDir = cpdDir;
        changeSupport.firePropertyChange("cpdDir", oldCpdDir, cpdDir);
    }

    public String getCpdCe() {
        return cpdCe;
    }

    public void setCpdCe(String cpdCe) {
        String oldCpdCe = this.cpdCe;
        this.cpdCe = cpdCe;
        changeSupport.firePropertyChange("cpdCe", oldCpdCe, cpdCe);
    }

    public String getCpdTr() {
        return cpdTr;
    }

    public void setCpdTr(String cpdTr) {
        String oldCpdTr = this.cpdTr;
        this.cpdTr = cpdTr;
        changeSupport.firePropertyChange("cpdTr", oldCpdTr, cpdTr);
    }

    public Date getCpdDate() {
        return cpdDate;
    }

    public void setCpdDate(Date cpdDate) {
        Date oldCpdDate = this.cpdDate;
        this.cpdDate = cpdDate;
        changeSupport.firePropertyChange("cpdDate", oldCpdDate, cpdDate);
    }

    public String getCpdNb() {
        return cpdNb;
    }

    public void setCpdNb(String cpdNb) {
        String oldCpdNb = this.cpdNb;
        this.cpdNb = cpdNb;
        changeSupport.firePropertyChange("cpdNb", oldCpdNb, cpdNb);
    }

    public boolean getVds() {
        return vds;
    }

    public void setVds(boolean vds) {
        boolean oldVds = this.vds;
        this.vds = vds;
        changeSupport.firePropertyChange("vds", oldVds, vds);
    }

    public String getVdsCe() {
        return vdsCe;
    }

    public void setVdsCe(String vdsCe) {
        String oldVdsCe = this.vdsCe;
        this.vdsCe = vdsCe;
        changeSupport.firePropertyChange("vdsCe", oldVdsCe, vdsCe);
    }

    public String getVdsTr() {
        return vdsTr;
    }

    public void setVdsTr(String vdsTr) {
        String oldVdsTr = this.vdsTr;
        this.vdsTr = vdsTr;
        changeSupport.firePropertyChange("vdsTr", oldVdsTr, vdsTr);
    }

    public Date getVdsDate() {
        return vdsDate;
    }

    public void setVdsDate(Date vdsDate) {
        Date oldVdsDate = this.vdsDate;
        this.vdsDate = vdsDate;
        changeSupport.firePropertyChange("vdsDate", oldVdsDate, vdsDate);
    }

    public boolean getBosec() {
        return bosec;
    }

    public void setBosec(boolean bosec) {
        boolean oldBosec = this.bosec;
        this.bosec = bosec;
        changeSupport.firePropertyChange("bosec", oldBosec, bosec);
    }

    public String getBosecCe() {
        return bosecCe;
    }

    public void setBosecCe(String bosecCe) {
        String oldBosecCe = this.bosecCe;
        this.bosecCe = bosecCe;
        changeSupport.firePropertyChange("bosecCe", oldBosecCe, bosecCe);
    }

    public Date getBosecDate() {
        return bosecDate;
    }

    public void setBosecDate(Date bosecDate) {
        Date oldBosecDate = this.bosecDate;
        this.bosecDate = bosecDate;
        changeSupport.firePropertyChange("bosecDate", oldBosecDate, bosecDate);
    }

    public boolean getKomo() {
        return komo;
    }

    public void setKomo(boolean komo) {
        boolean oldKomo = this.komo;
        this.komo = komo;
        changeSupport.firePropertyChange("komo", oldKomo, komo);
    }

    public String getKomoCe() {
        return komoCe;
    }

    public void setKomoCe(String komoCe) {
        String oldKomoCe = this.komoCe;
        this.komoCe = komoCe;
        changeSupport.firePropertyChange("komoCe", oldKomoCe, komoCe);
    }

    public Date getKomoDate() {
        return komoDate;
    }

    public void setKomoDate(Date komoDate) {
        Date oldKomoDate = this.komoDate;
        this.komoDate = komoDate;
        changeSupport.firePropertyChange("komoDate", oldKomoDate, komoDate);
    }

    public boolean getNf() {
        return nf;
    }

    public void setNf(boolean nf) {
        boolean oldNf = this.nf;
        this.nf = nf;
        changeSupport.firePropertyChange("nf", oldNf, nf);
    }

    public String getNfCe() {
        return nfCe;
    }

    public void setNfCe(String nfCe) {
        String oldNfCe = this.nfCe;
        this.nfCe = nfCe;
        changeSupport.firePropertyChange("nfCe", oldNfCe, nfCe);
    }

    public String getNfTr() {
        return nfTr;
    }

    public void setNfTr(String nfTr) {
        String oldNfTr = this.nfTr;
        this.nfTr = nfTr;
        changeSupport.firePropertyChange("nfTr", oldNfTr, nfTr);
    }

    public Date getNfDate() {
        return nfDate;
    }

    public void setNfDate(Date nfDate) {
        Date oldNfDate = this.nfDate;
        this.nfDate = nfDate;
        changeSupport.firePropertyChange("nfDate", oldNfDate, nfDate);
    }

    public boolean getKk() {
        return kk;
    }

    public void setKk(boolean kk) {
        boolean oldKk = this.kk;
        this.kk = kk;
        changeSupport.firePropertyChange("kk", oldKk, kk);
    }

    public String getKkCe() {
        return kkCe;
    }

    public void setKkCe(String kkCe) {
        String oldKkCe = this.kkCe;
        this.kkCe = kkCe;
        changeSupport.firePropertyChange("kkCe", oldKkCe, kkCe);
    }

    public Date getKkDate() {
        return kkDate;
    }

    public void setKkDate(Date kkDate) {
        Date oldKkDate = this.kkDate;
        this.kkDate = kkDate;
        changeSupport.firePropertyChange("kkDate", oldKkDate, kkDate);
    }

    public boolean getRf() {
        return rf;
    }

    public void setRf(boolean rf) {
        boolean oldRf = this.rf;
        this.rf = rf;
        changeSupport.firePropertyChange("rf", oldRf, rf);
    }

    public String getRfCe() {
        return rfCe;
    }

    public void setRfCe(String rfCe) {
        String oldRfCe = this.rfCe;
        this.rfCe = rfCe;
        changeSupport.firePropertyChange("rfCe", oldRfCe, rfCe);
    }

    public String getRfCert() {
        return rfCert;
    }

    public void setRfCert(String rfCert) {
        String oldRfCert = this.rfCert;
        this.rfCert = rfCert;
        changeSupport.firePropertyChange("rfCert", oldRfCert, rfCert);
    }

    public String getRfTr() {
        return rfTr;
    }

    public void setRfTr(String rfTr) {
        String oldRfTr = this.rfTr;
        this.rfTr = rfTr;
        changeSupport.firePropertyChange("rfTr", oldRfTr, rfTr);
    }

    public Date getRfDate() {
        return rfDate;
    }

    public void setRfDate(Date rfDate) {
        Date oldRfDate = this.rfDate;
        this.rfDate = rfDate;
        changeSupport.firePropertyChange("rfDate", oldRfDate, rfDate);
    }

    public String getRfNb() {
        return rfNb;
    }

    public void setRfNb(String rfNb) {
        String oldRfNb = this.rfNb;
        this.rfNb = rfNb;
        changeSupport.firePropertyChange("rfNb", oldRfNb, rfNb);
    }

    public String getRfNbN() {
        return rfNbN;
    }

    public void setRfNbN(String rfNbN) {
        String oldRfNbN = this.rfNbN;
        this.rfNbN = rfNbN;
        changeSupport.firePropertyChange("rfNbN", oldRfNbN, rfNbN);
    }

    public String getRfF() {
        return rfF;
    }

    public void setRfF(String rfF) {
        String oldRfF = this.rfF;
        this.rfF = rfF;
        changeSupport.firePropertyChange("rfF", oldRfF, rfF);
    }

    public boolean getRohs() {
        return rohs;
    }

    public void setRohs(boolean rohs) {
        boolean oldRohs = this.rohs;
        this.rohs = rohs;
        changeSupport.firePropertyChange("rohs", oldRohs, rohs);
    }

    public String getRohsCe() {
        return rohsCe;
    }

    public void setRohsCe(String rohsCe) {
        String oldRohsCe = this.rohsCe;
        this.rohsCe = rohsCe;
        changeSupport.firePropertyChange("rohsCe", oldRohsCe, rohsCe);
    }

    public String getRohsCert() {
        return rohsCert;
    }

    public void setRohsCert(String rohsCert) {
        String oldRohsCert = this.rohsCert;
        this.rohsCert = rohsCert;
        changeSupport.firePropertyChange("rohsCert", oldRohsCert, rohsCert);
    }

    public String getRohsTr() {
        return rohsTr;
    }

    public void setRohsTr(String rohsTr) {
        String oldRohsTr = this.rohsTr;
        this.rohsTr = rohsTr;
        changeSupport.firePropertyChange("rohsTr", oldRohsTr, rohsTr);
    }

    public Date getRohsDate() {
        return rohsDate;
    }

    public void setRohsDate(Date rohsDate) {
        Date oldRohsDate = this.rohsDate;
        this.rohsDate = rohsDate;
        changeSupport.firePropertyChange("rohsDate", oldRohsDate, rohsDate);
    }

    public String getRohsNb() {
        return rohsNb;
    }

    public void setRohsNb(String rohsNb) {
        String oldRohsNb = this.rohsNb;
        this.rohsNb = rohsNb;
        changeSupport.firePropertyChange("rohsNb", oldRohsNb, rohsNb);
    }

    public String getKind_bulb() {
        return kind_bulb;
    }

    public void setKind_bulb(String kind_bulb) {
        String oldKind_bulb = this.kind_bulb;
        this.kind_bulb = kind_bulb;
        changeSupport.firePropertyChange("kind_bulb", oldKind_bulb, kind_bulb);
    }

    public boolean getReach() {
        return reach;
    }

    public void setReach(boolean reach) {
        boolean oldReach = this.reach;
        this.reach = reach;
        changeSupport.firePropertyChange("reach", oldReach, reach);
    }

    public String getReachCe() {
        return reachCe;
    }

    public void setReachCe(String reachCe) {
        String oldReachCe = this.reachCe;
        this.reachCe = reachCe;
        changeSupport.firePropertyChange("reachCe", oldReachCe, reachCe);
    }

    public boolean getDoc() {
        return doc;
    }

    public void setDoc(boolean doc) {
        boolean oldDoc = this.doc;
        this.doc = doc;
        changeSupport.firePropertyChange("doc", oldDoc, doc);
    }

    public boolean getDoi() {
        return doi;
    }

    public void setDoi(boolean doi) {
        boolean oldDoi = this.doi;
        this.doi = doi;
        changeSupport.firePropertyChange("doi", oldDoi, doi);
    }

    public boolean getEup() {
        return eup;
    }

    public void setEup(boolean eup) {
        boolean oldEup = this.eup;
        this.eup = eup;
        changeSupport.firePropertyChange("eup", oldEup, eup);
    }

    public String getEupCe() {
        return eupCe;
    }

    public void setEupCe(String eupCe) {
        String oldEupCe = this.eupCe;
        this.eupCe = eupCe;
        changeSupport.firePropertyChange("eupCe", oldEupCe, eupCe);
    }

    public String getEupTr() {
        return eupTr;
    }

    public void setEupTr(String eupTr) {
        String oldEupTr = this.eupTr;
        this.eupTr = eupTr;
        changeSupport.firePropertyChange("eupTr", oldEupTr, eupTr);
    }

    public Date getEupDate() {
        return eupDate;
    }

    public void setEupDate(Date eupDate) {
        Date oldEupDate = this.eupDate;
        this.eupDate = eupDate;
        changeSupport.firePropertyChange("eupDate", oldEupDate, eupDate);
    }

    public String getEupStatus() {
        return eupStatus;
    }

    public void setEupStatus(String eupStatus) {
        String oldEupStatus = this.eupStatus;
        this.eupStatus = eupStatus;
        changeSupport.firePropertyChange("eupStatus", oldEupStatus, eupStatus);
    }

    public boolean getFlux() {
        return flux;
    }

    public void setFlux(boolean flux) {
        boolean oldFlux = this.flux;
        this.flux = flux;
        changeSupport.firePropertyChange("flux", oldFlux, flux);
    }

    public String getFluxTr() {
        return fluxTr;
    }

    public void setfluxTr(String fluxTr) {
        String oldFluxTr = this.fluxTr;
        this.fluxTr = fluxTr;
        changeSupport.firePropertyChange("fluxTr", oldFluxTr, fluxTr);
    }

    public boolean getUV() {
        return uv;
    }

    public void setUV(boolean uv) {
        boolean olduv = this.uv;
        this.uv = uv;
        changeSupport.firePropertyChange("uv", olduv, uv);
    }

    public String getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(String spectrum) {
        String oldspectrum = this.spectrum;
        this.spectrum = spectrum;
        changeSupport.firePropertyChange("spectrum", oldspectrum, spectrum);
    }

    public boolean getPah() {
        return pah;
    }

    public void setPah(boolean pah) {
        boolean oldPah = this.pah;
        this.pah = pah;
        changeSupport.firePropertyChange("pah", oldPah, pah);
    }

    public String getSpecial_use() {
        return special_use;
    }

    public void setSpecial_use(String special_use) {
        String oldSpecial_use = this.special_use;
        this.special_use = special_use;
        changeSupport.firePropertyChange("special_use", oldSpecial_use, special_use);
    }

    public boolean getAccent() {
        return accent;
    }

    public void setAaccent(boolean accent) {
        boolean oldaccent = this.accent;
        this.accent = accent;
        changeSupport.firePropertyChange("accent", oldaccent, accent);
    }

    public boolean getBatt() {
        return batt;
    }

    public void setBatt(boolean batt) {
        boolean oldBatt = this.batt;
        this.batt = batt;
        changeSupport.firePropertyChange("batt", oldBatt, batt);
    }

    public String getBattm() {
        return battm;
    }

    public void setBattm(String battm) {
        String oldbattm = this.battm;
        this.battm = battm;
        changeSupport.firePropertyChange("battm", oldbattm, battm);
    }

    public String getBattTr2() {
        return battTr2;
    }

    public void setBattTr2(String battTr2) {
        String oldbattTr2 = this.battTr2;
        this.battTr2 = battTr2;
        changeSupport.firePropertyChange("battTr2", oldbattTr2, battTr2);
    }

    public boolean getPhth() {
        return phth;
    }

    public void setPhth(boolean phth) {
        boolean oldPhth = this.phth;
        this.phth = phth;
        changeSupport.firePropertyChange("phth", oldPhth, phth);
    }

    public boolean getOem() {
        return oem;
    }

    public void setOem(boolean oem) {
        boolean oldOem = this.oem;
        this.oem = oem;
        changeSupport.firePropertyChange("oem", oldOem, oem);
    }

    public String getOemCe() {
        return oemCe;
    }

    public void setOemCe(String oemCe) {
        String oldOemCe = this.oemCe;
        this.oemCe = oemCe;
        changeSupport.firePropertyChange("oemCe", oldOemCe, oemCe);
    }

    public Date getOemDateFrom() {
        return oemDateFrom;
    }

    public void setOemDateFrom(Date oemDateFrom) {
        Date oldOemDateFrom = this.oemDateFrom;
        this.oemDateFrom = oemDateFrom;
        changeSupport.firePropertyChange("oemDateFrom", oldOemDateFrom, oemDateFrom);
    }

    public Date getOemDateTo() {
        return oemDateTo;
    }

    public void setOemDateTo(Date oemDateTo) {
        Date oldOemDateTo = this.oemDateTo;
        this.oemDateTo = oemDateTo;
        changeSupport.firePropertyChange("oemDateTo", oldOemDateTo, oemDateTo);
    }

    public boolean getGolden() {
        return golden;
    }

    public void setGolden(boolean golden) {
        boolean oldGolden = this.golden;
        this.golden = golden;
        changeSupport.firePropertyChange("golden", oldGolden, golden);
    }

    public String getGoldenT() {
        return goldenT;
    }

    public void setGoldenT(String goldenT) {
        String oldGoldenT = this.goldenT;
        this.goldenT = goldenT;
        changeSupport.firePropertyChange("goldenT", oldGoldenT, goldenT);
    }

    public String getModDate() {
        return modDate;
    }

    public void setModDate(String modDate) {
        String oldModDate = this.modDate;
        this.modDate = modDate;
        changeSupport.firePropertyChange("modDate", oldModDate, modDate);
    }

    public String getModWho() {
        return modWho;
    }

    public void setModWho(String modWho) {
        String oldModWho = this.modWho;
        this.modWho = modWho;
        changeSupport.firePropertyChange("modWho", oldModWho, modWho);
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        String oldValidDate = this.validDate;
        this.validDate = validDate;
        changeSupport.firePropertyChange("validDate", oldValidDate, validDate);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        String oldstatus = this.status;
        this.status = status;
        changeSupport.firePropertyChange("status", oldstatus, status);
    }

    public String getQCStatus() {
        return qc_status;
    }

    public void setQCStatus(String qc_status) {
        String oldqc_status = this.qc_status;
        this.qc_status = qc_status;
        changeSupport.firePropertyChange("qc_status", oldqc_status, qc_status);
    }

    public String getQMStatus() {
        return qm_status;
    }

    public void setQMStatus(String qm_status) {
        String oldqm_status = this.qm_status;
        this.qm_status = qm_status;
        changeSupport.firePropertyChange("qm_status", oldqm_status, qm_status);
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        String oldhierarchy = this.hierarchy;
        this.hierarchy = hierarchy;
        changeSupport.firePropertyChange("hierarchy", oldhierarchy, hierarchy);
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        String oldremarks = this.remarks;
        this.remarks = remarks;
        changeSupport.firePropertyChange("remarks", oldremarks, remarks);
    }

    public String getRemarks_Auth() {
        return remarks_auth;
    }

    public void setRemarks_Auth(String remarks_auth) {
        String oldremarks_auth = this.remarks_auth;
        this.remarks_auth = remarks_auth;
        changeSupport.firePropertyChange("remarks_auth", oldremarks_auth, remarks_auth);
    }

    public String getPAHCE() {
        return pahce;
    }

    public void setPAHCE(String pahce) {
        String oldpahce = this.pahce;
        this.pahce = pahce;
        changeSupport.firePropertyChange("pahce", oldpahce, pahce);
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        String oldean = this.ean;
        this.ean = ean;
        changeSupport.firePropertyChange("ean", oldean, ean);
    }

    public boolean getIncl() {
        return incl;
    }

    public void setIncl(boolean incl) {
        boolean oldincl = this.incl;
        this.incl = incl;
        changeSupport.firePropertyChange("incl", oldincl, incl);
    }

    public String getItem_Bulb() {
        return item_bulb;
    }

    public void setItem_Bulb(String item_bulb) {
        String olditem_bulb = this.item_bulb;
        this.item_bulb = item_bulb;
        changeSupport.firePropertyChange("item_bulb", olditem_bulb, item_bulb);
    }

    public boolean getInt_Led() {
        return int_led;
    }

    public void setInt_Led(boolean int_led) {
        boolean oldint_led = this.int_led;
        this.int_led = int_led;
        changeSupport.firePropertyChange("int_led", oldint_led, int_led);
    }

    public String getWattage() {
        return wattage;
    }

    public void setWattage(String wattage) {
        String oldwattage = this.wattage;
        this.wattage = wattage;
        changeSupport.firePropertyChange("wattage", oldwattage, wattage);
    }

    public String getWattage_Rated() {
        return wattage_rated;
    }

    public void setWattage_Rated(String wattage_rated) {
        String oldwattage_rated = this.wattage_rated;
        this.wattage_rated = wattage_rated;
        changeSupport.firePropertyChange("wattage_rated", oldwattage_rated, wattage_rated);
    }

    public String getLumen() {
        return lumen;
    }

    public void setLumen(String lumen) {
        String oldlumen = this.lumen;
        this.lumen = lumen;
        changeSupport.firePropertyChange("lumen", oldlumen, lumen);
    }

    public String getLumen_Rated() {
        return lumen_rated;
    }

    public void setLumen_Rated(String lumen_rated) {
        String oldlumen_rated = this.lumen_rated;
        this.lumen_rated = lumen_rated;
        changeSupport.firePropertyChange("lumen_rated", oldlumen_rated, lumen_rated);
    }

    public String getLifetime() {
        return lifetime;
    }

    public void setLifetime(String lifetime) {
        String oldlifetime = this.lifetime;
        this.lifetime = lifetime;
        changeSupport.firePropertyChange("lifetime", oldlifetime, lifetime);
    }

    public String getLifetime_Rated() {
        return lifetime_rated;
    }

    public void setLifetime_Rated(String lifetime_rated) {
        String oldlifetime_rated = this.lifetime_rated;
        this.lifetime_rated = lifetime_rated;
        changeSupport.firePropertyChange("lifetime_rated", oldlifetime_rated, lifetime_rated);
    }

    public String getSwicyc() {
        return swicyc;
    }

    public void setSwicyc(String swicyc) {
        String oldswicyc = this.swicyc;
        this.swicyc = swicyc;
        changeSupport.firePropertyChange("swicyc", oldswicyc, swicyc);
    }

    public String getKelvin() {
        return kelvin;
    }

    public void setKelvin(String kelvin) {
        String oldkelvin = this.kelvin;
        this.kelvin = kelvin;
        changeSupport.firePropertyChange("kelvin", oldkelvin, kelvin);
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        String oldra = this.ra;
        this.ra = ra;
        changeSupport.firePropertyChange("ra", oldra, ra);
    }

    public String getStar60() {
        return star60;
    }

    public void setStar60(String star60) {
        String oldstar60 = this.star60;
        this.star60 = star60;
        changeSupport.firePropertyChange("star60", oldstar60, star60);
    }

    public String getStart_Time() {
        return start_time;
    }

    public void setStart_Time(String start_time) {
        String oldstart_time = this.start_time;
        this.start_time = start_time;
        changeSupport.firePropertyChange("start_time", oldstart_time, start_time);
    }

    public String getColor_Cons() {
        return color_cons;
    }

    public void setColor_Cons(String color_cons) {
        String oldcolor_cons = this.color_cons;
        this.color_cons = color_cons;
        changeSupport.firePropertyChange("color_cons", oldcolor_cons, color_cons);
    }

    public String getCandela() {
        return candela;
    }

    public void setCandela(String candela) {
        String oldcandela = this.candela;
        this.candela = candela;
        changeSupport.firePropertyChange("candela", oldcandela, candela);
    }

    public String getBeam() {
        return beam;
    }

    public void setBeam(String beam) {
        String oldbeam = this.beam;
        this.beam = beam;
        changeSupport.firePropertyChange("beam", oldbeam, beam);
    }

    public String getBeam_R() {
        return beam_r;
    }

    public void setBeam_R(String beam_r) {
        String oldbeam_r = this.beam_r;
        this.beam_r = beam_r;
        changeSupport.firePropertyChange("beam_r", oldbeam_r, beam_r);
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        String oldcolour = this.colour;
        this.colour = colour;
        changeSupport.firePropertyChange("colour", oldcolour, colour);
    }

    public String getIndoor() {
        return indoor;
    }

    public void setIndoor(String indoor) {
        String oldindoor = this.indoor;
        this.indoor = indoor;
        changeSupport.firePropertyChange("indoor", oldindoor, indoor);
    }

    public String getDimension_L() {
        return dimension_l;
    }

    public void setDimension_L(String dimension_l) {
        String olddimension_l = this.dimension_l;
        this.dimension_l = dimension_l;
        changeSupport.firePropertyChange("dimension_l", olddimension_l, dimension_l);
    }

    public String getDimension_D() {
        return dimension_d;
    }

    public void setDimension_D(String dimension_d) {
        String olddimension_d = this.dimension_d;
        this.dimension_d = dimension_d;
        changeSupport.firePropertyChange("dimension_d", olddimension_d, dimension_d);
    }

    public String getDimension_Fi() {
        return dimension_fi;
    }

    public void setDimension_Fi(String dimension_fi) {
        String olddimension_fi = this.dimension_fi;
        this.dimension_fi = dimension_fi;
        changeSupport.firePropertyChange("dimension_fi", olddimension_fi, dimension_fi);
    }

    public String getPower_Factor() {
        return power_factor;
    }

    public void setPower_Factor(String power_factor) {
        String oldpower_factor = this.power_factor;
        this.power_factor = power_factor;
        changeSupport.firePropertyChange("power_factor", oldpower_factor, power_factor);
    }

    public String getLumen_Factor() {
        return lumen_factor;
    }

    public void setLumen_Factor(String lumen_factor) {
        String oldlumen_factor = this.lumen_factor;
        this.lumen_factor = lumen_factor;
        changeSupport.firePropertyChange("lumen_factor", oldlumen_factor, lumen_factor);
    }

    public String getDimmer() {
        return dimmer;
    }

    public void setDimmer(String dimmer) {
        String olddimmer = this.dimmer;
        this.dimmer = dimmer;
        changeSupport.firePropertyChange("dimmer", olddimmer, dimmer);
    }

    public String getEnclas() {
        return enclas;
    }

    public void setEnclas(String enclas) {
        String oldenclas = this.enclas;
        this.enclas = enclas;
        changeSupport.firePropertyChange("enclas", oldenclas, enclas);
    }

    public String getKwik() {
        return kwik;
    }

    public void setKwik(String kwik) {
        String oldkwik = this.kwik;
        this.kwik = kwik;
        changeSupport.firePropertyChange("kwik", oldkwik, kwik);
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        String oldvoltage = this.voltage;
        this.voltage = voltage;
        changeSupport.firePropertyChange("voltage", oldvoltage, voltage);
    }

    public String getAmpere() {
        return ampere;
    }

    public void setAmpere(String ampere) {
        String oldampere = this.ampere;
        this.ampere = ampere;
        changeSupport.firePropertyChange("ampere", oldampere, ampere);
    }

    public String getCompar() {
        return compar;
    }

    public void setCompar(String compar) {
        String oldcompar = this.compar;
        this.compar = compar;
        changeSupport.firePropertyChange("compar", oldcompar, compar);
    }

    public String getFittin() {
        return fittin;
    }

    public void setFittin(String fittin) {
        String oldfittin = this.fittin;
        this.fittin = fittin;
        changeSupport.firePropertyChange("fittin", oldfittin, fittin);
    }

    public String getLichtb() {
        return lichtb;
    }

    public void setLichtb(String lichtb) {
        String oldlichtb = this.lichtb;
        this.lichtb = lichtb;
        changeSupport.firePropertyChange("lichtb", oldlichtb, lichtb);
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        String oldshape = this.shape;
        this.shape = shape;
        changeSupport.firePropertyChange("shape", oldshape, shape);
    }

    public String getLed_Type() {
        return led_type;
    }

    public void setLed_Type(String led_type) {
        String oldled_type = this.led_type;
        this.led_type = led_type;
        changeSupport.firePropertyChange("led_type", oldled_type, led_type);
    }

    public String getLed_Number() {
        return led_number;
    }

    public void setLed_Number(String led_number) {
        String oldled_number = this.led_number;
        this.led_number = led_number;
        changeSupport.firePropertyChange("led_number", oldled_number, led_number);
    }

    public Date getTest_Date() {
        return test_date;
    }

    public void setTest_Date(Date test_date) {
        Date oldtest_date = this.test_date;
        this.test_date = test_date;
        changeSupport.firePropertyChange("test_date", oldtest_date, test_date);
    }

    public Date getT6000H_Date() {
        return t6000h_date;
    }

    public void setT6000H_Date(Date t6000h_date) {
        Date oldt6000h_date = this.t6000h_date;
        this.t6000h_date = t6000h_date;
        changeSupport.firePropertyChange("t6000h_date", oldt6000h_date, t6000h_date);
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        String oldbuyer = this.buyer;
        this.buyer = buyer;
        changeSupport.firePropertyChange("buyer", oldbuyer, buyer);
    }

    public String getMonster() {
        return monster;
    }

    public void setMonster(String monster) {
        String oldmonster = this.monster;
        this.monster = monster;
        changeSupport.firePropertyChange("monster", oldmonster, monster);
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        String oldcontact = this.contact;
        this.contact = contact;
        changeSupport.firePropertyChange("contact", oldcontact, contact);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String oldemail = this.email;
        this.email = email;
        changeSupport.firePropertyChange("email", oldemail, email);
    }

    public boolean getAssortment() {
        return assortment;
    }

    public void setAssortment(boolean assortment) {
        boolean oldassortment = this.assortment;
        this.assortment = assortment;
        changeSupport.firePropertyChange("assortment", oldassortment, assortment);
    }

    public boolean getPromotion() {
        return promotion;
    }

    public void setPromotion(boolean promotion) {
        boolean oldpromotion = this.promotion;
        this.promotion = promotion;
        changeSupport.firePropertyChange("promotion", oldpromotion, promotion);
    }

    public boolean getNewitem() {
        return newitem;
    }

    public void setNewitem(boolean newitem) {
        boolean oldnewitem = this.newitem;
        this.newitem = newitem;
        changeSupport.firePropertyChange("newitem", oldnewitem, newitem);
    }

    public Date getCheckdate() {
        return checkdate;
    }

    public void setCheckdate(Date checkdate) {
        Date oldcheckdate = this.checkdate;
        this.checkdate = checkdate;
        changeSupport.firePropertyChange("checkdate", oldcheckdate, checkdate);
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        Date oldupdate = this.update;
        this.update = update;
        changeSupport.firePropertyChange("update", oldupdate, update);
    }

    public boolean getCertifcheck() {
        return certifcheck;
    }

    public void setCertifcheck(boolean certifcheck) {
        boolean oldcertifcheck = this.certifcheck;
        this.certifcheck = certifcheck;
        changeSupport.firePropertyChange("certifcheck", oldcertifcheck, certifcheck);
    }

    public boolean getTechnqccheck() {
        return technqccheck;
    }

    public void setTechnqccheck(boolean technqccheck) {
        boolean oldtechnqccheck = this.technqccheck;
        this.technqccheck = technqccheck;
        changeSupport.firePropertyChange("technqccheck", oldtechnqccheck, technqccheck);
    }

    public boolean getFunctcheck() {
        return functcheck;
    }

    public void setFunctcheck(boolean functcheck) {
        boolean oldfunctcheck = this.functcheck;
        this.functcheck = functcheck;
        changeSupport.firePropertyChange("functcheck", oldfunctcheck, functcheck);
    }

    public boolean getTechntdcheck() {
        return techntdcheck;
    }

    public void setTechntdcheck(boolean techntdcheck) {
        boolean oldtechntdcheck = this.techntdcheck;
        this.techntdcheck = techntdcheck;
        changeSupport.firePropertyChange("techntdcheck", oldtechntdcheck, techntdcheck);
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        String oldconclusion = this.conclusion;
        this.conclusion = conclusion;
        changeSupport.firePropertyChange("conclusion", oldconclusion, conclusion);
    }

    public String getMAX_WATT1() {
        return max_watt1;
    }

    public void setMAX_WATT1(String max_watt1) {
        String oldMAX_WATT1 = this.max_watt1;
        this.max_watt1 = max_watt1;
        changeSupport.firePropertyChange("max_watt1", oldMAX_WATT1, max_watt1);
    }

    public String getTYPE_BULB1() {
        return type_bulb1;
    }

    public void setTYPE_BULB1(String type_bulb1) {
        String oldTYPE_BULB1 = this.type_bulb1;
        this.type_bulb1 = type_bulb1;
        changeSupport.firePropertyChange("type_bulb1", oldTYPE_BULB1, type_bulb1);
    }

    public String getMAX_WATT2() {
        return max_watt2;
    }

    public void setMAX_WATT2(String max_watt2) {
        String oldMAX_WATT2 = this.max_watt2;
        this.max_watt2 = max_watt2;
        changeSupport.firePropertyChange("max_watt2", oldMAX_WATT2, max_watt2);
    }

    public String getTYPE_BULB2() {
        return type_bulb2;
    }

    public void setTYPE_BULB2(String type_bulb2) {
        String oldTYPE_BULB2 = this.type_bulb2;
        this.type_bulb2 = type_bulb2;
        changeSupport.firePropertyChange("type_bulb2", oldTYPE_BULB2, type_bulb2);
    }

    public String getFIXTURE_TYPE() {
        return fixture_type;
    }

    public void setFIXTURE_TYPE(String fixture_type) {
        String oldFIXTURE_TYPE = this.fixture_type;
        this.fixture_type = fixture_type;
        changeSupport.firePropertyChange("fixture_type", oldFIXTURE_TYPE, fixture_type);
    }

    public String getAPPLIANCE_CLASS1() {
        return appliance_class1;
    }

    public void setAPPLIANCE_CLASS1(String appliance_class1) {
        String oldAPPLIANCE_CLASS1 = this.appliance_class1;
        this.appliance_class1 = appliance_class1;
        changeSupport.firePropertyChange("appliance_class1", oldAPPLIANCE_CLASS1, appliance_class1);
    }

    public String getAPPLIANCE_CLASS2() {
        return appliance_class2;
    }

    public void setAPPLIANCE_CLASS2(String appliance_class2) {
        String oldAPPLIANCE_CLASS2 = this.appliance_class2;
        this.appliance_class2 = appliance_class2;
        changeSupport.firePropertyChange("appliance_class2", oldAPPLIANCE_CLASS2, appliance_class2);
    }

    public String getFIXTURE_SIZE() {
        return fixture_size;
    }

    public void setFIXTURE_SIZE(String fixture_size) {
        String oldFIXTURE_SIZE = this.fixture_size;
        this.fixture_size = fixture_size;
        changeSupport.firePropertyChange("fixture_size", oldFIXTURE_SIZE, fixture_size);
    }

    public String getBULB_CLASS() {
        return bulb_class;
    }

    public void setBULB_CLASS(String bulb_class) {
        String oldBULB_CLASS = this.bulb_class;
        this.bulb_class = bulb_class;
        changeSupport.firePropertyChange("bulb_class", oldBULB_CLASS, bulb_class);
    }

    public boolean getMAINS() {
        return mains;
    }

    public void setMAINS(boolean mains) {
        boolean oldmains = this.mains;
        this.mains = mains;
        changeSupport.firePropertyChange("mains", oldmains, mains);
    }

    public String getMAINS_IN_VOLT() {
        return mains_in_volt;
    }

    public void setMAINS_IN_VOLT(String mains_in_volt) {
        String oldmains_in_volt = this.mains_in_volt;
        this.mains_in_volt = mains_in_volt;
        changeSupport.firePropertyChange("mains_in_volt", oldmains_in_volt, mains_in_volt);
    }

    public String getMAINS_IN_WATT() {
        return mains_in_watt;
    }

    public void setMAINS_IN_WATT(String mains_in_watt) {
        String oldmains_in_watt = this.mains_in_watt;
        this.mains_in_watt = mains_in_watt;
        changeSupport.firePropertyChange("mains_in_watt", oldmains_in_watt, mains_in_watt);
    }

    public String getMAINS_IN_PLUG() {
        return mains_in_plug;
    }

    public void setMAINS_IN_PLUG(String mains_in_plug) {
        String oldmains_in_plug = this.mains_in_plug;
        this.mains_in_plug = mains_in_plug;
        changeSupport.firePropertyChange("mains_in_plug", oldmains_in_plug, mains_in_plug);
    }

    public String getIP_RATE() {
        return ip_rate;
    }

    public void setIP_RATE(String ip_rate) {
        String oldIP_RATE = this.ip_rate;
        this.ip_rate = ip_rate;
        changeSupport.firePropertyChange("ip_rate", oldIP_RATE, ip_rate);
    }

    public String getMAINS_CLASS() {
        return mains_class;
    }

    public void setMAINS_CLASS(String mains_class) {
        String oldmains_class = this.mains_class;
        this.mains_class = mains_class;
        changeSupport.firePropertyChange("mains_class", oldmains_class, mains_class);
    }

    public String getMAINS_OUT_WATT() {
        return mains_out_watt;
    }

    public void setMAINS_OUT_WATT(String mains_out_watt) {
        String oldmains_out_watt = this.mains_out_watt;
        this.mains_out_watt = mains_out_watt;
        changeSupport.firePropertyChange("mains_out_watt", oldmains_out_watt, mains_out_watt);
    }

    public String getMAINS_OUT_PLUG() {
        return mains_out_plug;
    }

    public void setMAINS_OUT_PLUG(String mains_out_plug) {
        String oldmains_out_plug = this.mains_out_plug;
        this.mains_out_plug = mains_out_plug;
        changeSupport.firePropertyChange("mains_out_plug", oldmains_out_plug, mains_out_plug);
    }

    public boolean getADAPTOR1() {
        return adaptor1;
    }

    public void setADAPTOR1(boolean adaptor1) {
        boolean oldadaptor1 = this.adaptor1;
        this.adaptor1 = adaptor1;
        changeSupport.firePropertyChange("adaptor1", oldadaptor1, adaptor1);
    }

    public boolean getADAPTOR2() {
        return adaptor2;
    }

    public void setADAPTOR2(boolean adaptor2) {
        boolean oldadaptor2 = this.adaptor2;
        this.adaptor2 = adaptor2;
        changeSupport.firePropertyChange("adaptor2", oldadaptor2, adaptor2);
    }

    public String getADAPTOR_TYPE1() {
        return adaptor_type1;
    }

    public void setADAPTOR_TYPE1(String adaptor_type1) {
        String oldadaptor_type1 = this.adaptor_type1;
        this.adaptor_type1 = adaptor_type1;
        changeSupport.firePropertyChange("adaptor_type1", oldadaptor_type1, adaptor_type1);
    }

    public String getADAPTOR_TYPE2() {
        return adaptor_type2;
    }

    public void setADAPTOR_TYPE2(String adaptor_type2) {
        String oldadaptor_type2 = this.adaptor_type2;
        this.adaptor_type2 = adaptor_type2;
        changeSupport.firePropertyChange("adaptor_type2", oldadaptor_type2, adaptor_type2);
    }

    public String getIN_VOLT1() {
        return in_volt1;
    }

    public void setIN_VOLT1(String in_volt1) {
        String oldIN_VOLT1 = this.in_volt1;
        this.in_volt1 = in_volt1;
        changeSupport.firePropertyChange("in_volt1", oldIN_VOLT1, in_volt1);
    }

    public String getOUT_VOLT1() {
        return out_volt1;
    }

    public void setOUT_VOLT1(String out_volt1) {
        String oldOUT_VOLT1 = this.out_volt1;
        this.out_volt1 = out_volt1;
        changeSupport.firePropertyChange("out_volt1", oldOUT_VOLT1, out_volt1);
    }

    public String getIN_VOLT2() {
        return in_volt2;
    }

    public void setIN_VOLT2(String in_volt2) {
        String oldIN_VOLT2 = this.in_volt2;
        this.in_volt2 = in_volt2;
        changeSupport.firePropertyChange("in_volt2", oldIN_VOLT2, in_volt2);
    }

    public String getOUT_VOLT2() {
        return out_volt2;
    }

    public void setOUT_VOLT2(String out_volt2) {
        String oldOUT_VOLT2 = this.out_volt2;
        this.out_volt2 = out_volt2;
        changeSupport.firePropertyChange("out_volt2", oldOUT_VOLT2, out_volt2);
    }

    public String getIN_AMP1() {
        return in_amp1;
    }

    public void setIN_AMP1(String in_amp1) {
        String oldIN_AMP1 = this.in_amp1;
        this.in_amp1 = in_amp1;
        changeSupport.firePropertyChange("in_amp1", oldIN_AMP1, in_amp1);
    }

    public String getOUT_AMP1() {
        return out_amp1;
    }

    public void setOUT_AMP1(String out_amp1) {
        String oldOUT_AMP1 = this.out_amp1;
        this.out_amp1 = out_amp1;
        changeSupport.firePropertyChange("out_amp1", oldOUT_AMP1, out_amp1);
    }

    public String getIN_AMP2() {
        return in_amp2;
    }

    public void setIN_AMP2(String in_amp2) {
        String oldIN_AMP2 = this.in_amp2;
        this.in_amp2 = in_amp2;
        changeSupport.firePropertyChange("in_amp2", oldIN_AMP2, in_amp2);
    }

    public String getOUT_AMP2() {
        return out_amp2;
    }

    public void setOUT_AMP2(String out_amp2) {
        String oldOUT_AMP2 = this.out_amp2;
        this.out_amp2 = out_amp2;
        changeSupport.firePropertyChange("out_amp2", oldOUT_AMP2, out_amp2);
    }

    public String getIN_WATT1() {
        return in_watt1;
    }

    public void setIN_WATT1(String in_watt1) {
        String oldIN_WATT1 = this.in_watt1;
        this.in_watt1 = in_watt1;
        changeSupport.firePropertyChange("in_watt1", oldIN_WATT1, in_watt1);
    }

    public String getOUT_WATT1() {
        return out_watt1;
    }

    public void setOUT_WATT1(String out_watt1) {
        String oldOUT_WATT1 = this.out_watt1;
        this.out_watt1 = out_watt1;
        changeSupport.firePropertyChange("out_watt1", oldOUT_WATT1, out_watt1);
    }

    public String getIN_WATT2() {
        return in_watt2;
    }

    public void setIN_WATT2(String in_watt2) {
        String oldIN_WATT2 = this.in_watt2;
        this.in_watt2 = in_watt2;
        changeSupport.firePropertyChange("in_watt2", oldIN_WATT2, in_watt2);
    }

    public String getOUT_WATT2() {
        return out_watt2;
    }

    public void setOUT_WATT2(String out_watt2) {
        String oldOUT_WATT2 = this.out_watt2;
        this.out_watt2 = out_watt2;
        changeSupport.firePropertyChange("out_watt2", oldOUT_WATT2, out_watt2);
    }

    public boolean getOUT_LOCK() {
        return out_lock;
    }

    public void setOUT_LOCK(boolean out_lock) {
        boolean oldOUT_LOCK = this.out_lock;
        this.out_lock = out_lock;
        changeSupport.firePropertyChange("out_lock", oldOUT_LOCK, out_lock);
    }

    public String getBATT_QUA1() {
        return batt_qua1;
    }

    public void setBATT_QUA1(String batt_qua1) {
        String oldBATT_QUA1 = this.batt_qua1;
        this.batt_qua1 = batt_qua1;
        changeSupport.firePropertyChange("batt_qua1", oldBATT_QUA1, batt_qua1);
    }

    public String getBATT_BRAND1() {
        return batt_brand1;
    }

    public void setBATT_BRAND1(String batt_brand1) {
        String oldBatt_brand1 = this.batt_brand1;
        this.batt_brand1 = batt_brand1;
        changeSupport.firePropertyChange("batt_brand1", oldBatt_brand1, batt_brand1);
    }

    public boolean getBATT_ACCU1() {
        return batt_accu1;
    }

    public void setBATT_ACCU1(boolean batt_accu1) {
        boolean oldBATT_ACCU1 = this.batt_accu1;
        this.batt_accu1 = batt_accu1;
        changeSupport.firePropertyChange("batt_accu1", oldBATT_ACCU1, batt_accu1);
    }

    public String getBATT_CAP1() {
        return batt_cap1;
    }

    public void setBATT_CAP1(String batt_cap1) {
        String oldBATT_CAP1 = this.batt_cap1;
        this.batt_cap1 = batt_cap1;
        changeSupport.firePropertyChange("batt_cap1", oldBATT_CAP1, batt_cap1);
    }

    public String getBATT_TYPE1() {
        return batt_type1;
    }

    public void setBATT_TYPE1(String batt_type1) {
        String oldBATT_TYPE1 = this.batt_type1;
        this.batt_type1 = batt_type1;
        changeSupport.firePropertyChange("batt_type1", oldBATT_TYPE1, batt_type1);
    }

    public String getBATT_SIZE1() {
        return batt_size1;
    }

    public void setBATT_SIZE1(String batt_size1) {
        String oldBATT_SIZE1 = this.batt_size1;
        this.batt_size1 = batt_size1;
        changeSupport.firePropertyChange("batt_size1", oldBATT_SIZE1, batt_size1);
    }

    public String getBATT_VOLT1() {
        return batt_volt1;
    }

    public void setBATT_VOLT1(String batt_volt1) {
        String oldBATT_VOLT1 = this.batt_volt1;
        this.batt_volt1 = batt_volt1;
        changeSupport.firePropertyChange("batt_volt1", oldBATT_VOLT1, batt_volt1);
    }

    public boolean getBATT_REPL1() {
        return batt_repl1;
    }

    public void setBATT_REPL1(boolean batt_repl1) {
        boolean oldBATT_REPL1 = this.batt_repl1;
        this.batt_repl1 = batt_repl1;
        changeSupport.firePropertyChange("batt_repl1", oldBATT_REPL1, batt_repl1);
    }

    public boolean getBATT2() {
        return batt2;
    }

    public void setBATT2(boolean batt2) {
        boolean oldBATT2 = this.batt2;
        this.batt2 = batt2;
        changeSupport.firePropertyChange("batt2", oldBATT2, batt2);
    }

    public String getBATT_QUA2() {
        return batt_qua2;
    }

    public void setBATT_QUA2(String batt_qua2) {
        String oldBATT_QUA2 = this.batt_qua2;
        this.batt_qua2 = batt_qua2;
        changeSupport.firePropertyChange("batt_qua2", oldBATT_QUA2, batt_qua2);
    }

    public String getBATT_BRAND2() {
        return batt_brand2;
    }

    public void setBATT_BRAND2(String batt_brand2) {
        String oldBatt_brand2 = this.batt_brand2;
        this.batt_brand2 = batt_brand2;
        changeSupport.firePropertyChange("batt_brand2", oldBatt_brand2, batt_brand2);
    }

    public boolean getBATT_ACCU2() {
        return batt_accu2;
    }

    public void setBATT_ACCU2(boolean batt_accu2) {
        boolean oldBATT_ACCU2 = this.batt_accu2;
        this.batt_accu2 = batt_accu2;
        changeSupport.firePropertyChange("batt_accu2", oldBATT_ACCU2, batt_accu2);
    }

    public String getBATT_CAP2() {
        return batt_cap2;
    }

    public void setBATT_CAP2(String batt_cap2) {
        String oldBATT_CAP2 = this.batt_cap2;
        this.batt_cap2 = batt_cap2;
        changeSupport.firePropertyChange("batt_cap2", oldBATT_CAP2, batt_cap2);
    }

    public String getBATT_TYPE2() {
        return batt_type2;
    }

    public void setBATT_TYPE2(String batt_type2) {
        String oldBATT_TYPE2 = this.batt_type2;
        this.batt_type2 = batt_type2;
        changeSupport.firePropertyChange("batt_type2", oldBATT_TYPE2, batt_type2);
    }

    public String getBATT_SIZE2() {
        return batt_size2;
    }

    public void setBATT_SIZE2(String batt_size2) {
        String oldBATT_SIZE2 = this.batt_size2;
        this.batt_size2 = batt_size2;
        changeSupport.firePropertyChange("batt_size2", oldBATT_SIZE2, batt_size2);
    }

    public String getBATT_VOLT2() {
        return batt_volt2;
    }

    public void setBATT_VOLT2(String batt_volt2) {
        String oldBATT_VOLT2 = this.batt_volt2;
        this.batt_volt2 = batt_volt2;
        changeSupport.firePropertyChange("batt_volt2", oldBATT_VOLT2, batt_volt2);
    }

    public boolean getBATT_REPL2() {
        return batt_repl2;
    }

    public void setBATT_REPL2(boolean batt_repl2) {
        boolean oldBATT_REPL2 = this.batt_repl2;
        this.batt_repl2 = batt_repl2;
        changeSupport.firePropertyChange("batt_repl2", oldBATT_REPL2, batt_repl2);
    }

    public String getSENSOR_TYPE() {
        return sensor_type;
    }

    public void setSENSOR_TYPE(String sensor_type) {
        String oldsensor_type = this.sensor_type;
        this.sensor_type = sensor_type;
        changeSupport.firePropertyChange("sensor_type", oldsensor_type, sensor_type);
    }

    public String getSENSOR_SIZE() {
        return sensor_size;
    }

    public void setSENSOR_SIZE(String sensor_size) {
        String oldsensor_size = this.sensor_size;
        this.sensor_size = sensor_size;
        changeSupport.firePropertyChange("sensor_size", oldsensor_size, sensor_size);
    }

    public String getPIXELS() {
        return pixels;
    }

    public void setPIXELS(String pixels) {
        String oldpixels = this.pixels;
        this.pixels = pixels;
        changeSupport.firePropertyChange("pixels", oldpixels, pixels);
    }

    public String getIRLED() {
        return irled;
    }

    public void setIRLED(String irled) {
        String oldirled = this.irled;
        this.irled = irled;
        changeSupport.firePropertyChange("irled", oldirled, irled);
    }

    public String getSCREEN_SIZE() {
        return screen_size;
    }

    public void setSCREEN_SIZE(String screen_size) {
        String oldscreen_size = this.screen_size;
        this.screen_size = screen_size;
        changeSupport.firePropertyChange("screen_size", oldscreen_size, screen_size);
    }

    public String getTV_LINES() {
        return tv_lines;
    }

    public void setTV_LINES(String tv_lines) {
        String oldtv_lines = this.tv_lines;
        this.tv_lines = tv_lines;
        changeSupport.firePropertyChange("tv_lines", oldtv_lines, tv_lines);
    }

    public String getBELL_SOUND() {
        return bell_sound;
    }

    public void setBELL_SOUND(String bell_sound) {
        String oldbell_sound = this.bell_sound;
        this.bell_sound = bell_sound;
        changeSupport.firePropertyChange("bell_sound", oldbell_sound, bell_sound);
    }

    public String getBELL_TEMP() {
        return bell_temp;
    }

    public void setBELL_TEMP(String bell_temp) {
        String oldbell_temp = this.bell_temp;
        this.bell_temp = bell_temp;
        changeSupport.firePropertyChange("bell_temp", oldbell_temp, bell_temp);
    }

    public boolean getCE() {
        return CE;
    }

    public void setCE(boolean CE) {
        boolean oldCE = this.CE;
        this.CE = CE;
        changeSupport.firePropertyChange("CE", oldCE, CE);
    }

    public boolean getWEEE() {
        return WEEE;
    }

    public void setWEEE(boolean WEEE) {
        boolean oldWEEE = this.WEEE;
        this.WEEE = WEEE;
        changeSupport.firePropertyChange("WEEE", oldWEEE, WEEE);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Items)) {
            return false;
        }
        Items other = (Items) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "desktopapplication1.Items[ id=" + id + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
