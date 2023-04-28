package com.hermes.chm.integraciones.marketplace.cencosud;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import com.hermes.chm.api.bl.CategoryService;
import com.hermes.chm.api.bl.CredentialsService;
import com.hermes.chm.api.bl.MailSenderService;
import com.hermes.chm.api.bl.PublicationResultService;
import com.hermes.chm.api.bl.impl.StockModelServiceImpl;
import com.hermes.chm.api.constants.Credenciales;
import com.hermes.chm.api.constants.PedidoConstants;
import com.hermes.chm.api.constants.TipoModelo;
import com.hermes.chm.api.constants.XMLOrderConstants;
import com.hermes.chm.api.dto.CredentialsSiteChannelDto;
import com.hermes.chm.api.exception.HermesCoreCallException;
import com.hermes.chm.api.exception.IntegrationExecuteException;
import com.hermes.chm.api.model.chm.CanalSiteBD;
import com.hermes.chm.api.model.chm.CategoriaCanalAtributoBD;
import com.hermes.chm.api.model.chm.CategoriaCanalBD;
import com.hermes.chm.api.model.chm.ModeloAtributoBD;
import com.hermes.chm.api.model.chm.ModeloBD;
import com.hermes.chm.api.model.chmmongo.CanalSiteProductoM;
import com.hermes.chm.api.model.chmmongo.ResultadoPublicacionM;
import com.hermes.chm.api.model.chmmongo.ResultadoPublicacionPedidoM;
import com.hermes.chm.api.model.chmmongo.ResultadoPublicacionProductoM;
import com.hermes.chm.api.model.chmmongo.ResultadoPublicacionProductoVariantM;
import com.hermes.chm.api.repository.chm.CategoriaCanalAtributoRepository;
import com.hermes.chm.api.repository.chm.CategoriaCanalRepository;
import com.hermes.chm.api.repository.chm.ModeloAtributoRepository;
import com.hermes.chm.api.repository.chm.ModeloRepository;
import com.hermes.chm.api.repository.chm.PublicacionRepository;
import com.hermes.chm.api.repository.chmmongo.CanalSiteProductoRepositoryM;
import com.hermes.chm.api.repository.chmmongo.ResultadoPublicacionProductoRepositoryM;
import com.hermes.chm.api.repository.chmmongo.ResultadoPublicacionRepositoryM;
import com.hermes.chm.api.repository.hermes.SiteRepository;
import com.hermes.chm.api.util.StringTools;
import com.hermes.chm.commons.BeanDescriber;
import com.hermes.chm.commons.FileTools;
import com.hermes.chm.commons.HermesCore;
import com.hermes.chm.commons.HermesService;
import com.hermes.chm.commons.HttpClientRestTools;
import com.hermes.chm.commons.HttpClientTools;
import com.hermes.chm.commons.ProcesoInfo;
import com.hermes.chm.commons.ProductTools;
import com.hermes.chm.commons.TypeTools;
import com.hermes.chm.commons.XmlTools;
import com.hermes.chm.commons.monitor.Monitor;
import com.hermes.chm.integraciones.marketplace.IIntegracionMarketplace;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.CencosudProvider;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSCategory;
import com.hermes.chm.integraciones.marketplace.cencosud.connector.models.CSProduct;
import com.hermes.chm.integraciones.marketplace.mercadolibre.connector.CategoryMercadoLibreConnector;
import com.hermes.chm.integraciones.marketplace.mercadolibre.connector.OrderMercadoLibreConnector;
import com.hermes.chm.integraciones.marketplace.mercadolibre.connector.ShippingMercadoLivreConnector;
import com.hermes.chm.integraciones.marketplace.mercadolibre.model.LogisticType;
import com.hermes.core.commons.PublicConstants;
import com.hermes.core.dto.AlmacenDto;
import com.hermes.core.dto.PedidoDto;
import com.hermes.core.dto.PedidoEstadoHistoricoDto;
import com.hermes.core.dto.PedidoLineaDto;
import com.hermes.core.dto.ProductoCollectionDto;
import com.hermes.core.dto.SiteDto;
import com.hermes.core.dto.StockDto;
import com.hermes.core.dto.SubProductoDto;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.hermes.chm.integraciones.marketplace.mercadolibre.Meli;
import com.hermes.chm.integraciones.marketplace.mercadolibre.MeliException;

import com.hermes.chm.integraciones.marketplace.mercadolibre.MercadoLibreConstants;
import com.hermes.chm.integraciones.marketplace.mercadolibre.NumberTools;
import com.hermes.chm.integraciones.marketplace.mercadolibre.ValidarRut;

public class CencosudAdapter implements IIntegracionMarketplace {

        private static final Logger log = Logger.getLogger(CencosudAdapter.class);
        private static final int MAXIMUM_COMPOSICION_SIZE = 254;
        private static final String DOCUMENT_TYPE_CNPJ = "CNPJ";

        @Autowired
        private OrderMercadoLibreConnector orderMercadoLibreConnector;

        @Autowired
        private HermesCore hermesCore;

        @Autowired
        private Environment env;

        @Autowired
        private HermesService hermesService;

        @Autowired
        SiteRepository siteRepository;

        @Autowired
        private CredentialsService credentialsService;

        @Autowired
        CanalSiteProductoRepositoryM canalSiteProductoRepositoryM;

        @Autowired
        PublicacionRepository publicacionRepository;

        @Autowired
        CategoriaCanalRepository categoriaCanalRepository;

        @Autowired
        CategoriaCanalAtributoRepository categoriaCanalAtributoRepository;

        @Autowired
        CategoryMercadoLibreConnector categoryMercadoLibreConnector;

        @Autowired
        PublicationResultService publicationResultService;

        @Autowired
        CategoryService categoryService;

        @Autowired
        StockModelServiceImpl stockModelServiceImpl;

        @Autowired
        MailSenderService mailSenderService;

        @Autowired
        ProductTools productTools;

        @Autowired
        Monitor monitor;

        @Value("${ruta.imagenes.modalia.articulos.nuevo}")
        private String rutaImagenesModaliaArticulosNuevos;

        @Value("${hermes.login.ws}")
        private String hermesLoginWs;

        @Value("${images.base.url}")
        private String baseImages;

        @Value("${hermes.password.ws}")
        private String hermesPasswordWs;

        @Value("${hermes.url.ws}")
        private String hermesUrlWs;

        @Value("${mercado.libre.url}")
        private String mercadoLibreUrl;

        @Value("${ruta.guias.mercadolibre}")
        private String rutaGuias;

        @Value("${dias.feedback.mercadolibre}")
        private Long diasFeedback;

        @Value("${mercado.libre.estados.update}")
        String idEstadosPedidoUpdate;

        @Value("${url.hermes.admin}")
        private String URL_HERMES_ADMIN;

        @Value("${mercado.libre.estados.feedback}")
        private String idEstadosPedidoFeedback;
        private static final Map<String, String> coloresBrasil;

        @Autowired
        private ModeloAtributoRepository modeloAtributoRepository;

        @Autowired
        private ShippingMercadoLivreConnector shippingMercadoLivreConnector;

        @Autowired
        ModeloRepository modeloRepository;

        @Value("${id.atributo.envio.coste-envio}")
        public Long ID_ATR_COSTE_DEVOLUCION;

        @Value("${id.atributo.envio.politica-envio}")
        public Long ID_ATR_ENVIO_POLITICA_ENVIO;

        @Value("#{${mercado.atributos.map}}")
        Map<String, String> mapAtributos;

        static {
                Map<String, String> aMap = new HashMap<String, String>();
                aMap.put("AMARILLO", "Amarelo");
                aMap.put("AZUL", "Azul");
                aMap.put("BEIGE", "Bege");
                aMap.put("BLANCO", "Branco");
                aMap.put("BRONZE", "Bronze");
                aMap.put("CARAMELO", "Caramelo");
                aMap.put("GRIS", "Cinza");
                aMap.put("COBRA", "Cobra");
                aMap.put("CORAL", "Coral");
                aMap.put("DORADO", "Dourado");
                aMap.put("FLORAL", "Floral");
                aMap.put("GRAFITO", "Grafite");
                aMap.put("INCOLOR", "Incolor");
                aMap.put("NARANJA", "Laranja");
                aMap.put("MARRON", "Marrom");
                aMap.put("MULTICOLOR", "Multicolorido");
                aMap.put("DESNUDO", "Nude");
                aMap.put("BLANQUECINO", "Off-white");
                aMap.put("JAGUAR", "Onça");
                aMap.put("JAGUAR CLARO", "Onça claro");
                aMap.put("JAGUAR OSCURO", "Onça escuro");
                aMap.put("Poás", "Poá");
                aMap.put("PLATA", "Prata");
                aMap.put("NEGRO", "Preto");
                aMap.put("ROSA", "Rosa");
                aMap.put("PURPURA", "Roxo");
                aMap.put("UNICO", "Único");
                aMap.put("VERDE", "Verde");
                aMap.put("VERDE OLIVA", "Verde Oliva");
                aMap.put("ROJO", "Vermelho");
                aMap.put("VINO", "Vinho");
                aMap.put("AJEDREZ", "Xadrez");
                aMap.put("CEBRA", "Zebra");
                aMap.put("PLATA VIEJA", "Prata Envelhecido");
                aMap.put("OROVIEJO", "Ouro Envelhecido");
                aMap.put("COBRE", "Cobre");
                aMap.put("VINO", "Bordô");
                aMap.put("AZUL MARINO", "Azul Marinho");
                aMap.put("CAFE", "Café");
                aMap.put("PELIRROJO", "Ruivo");
                aMap.put("RUBIO", "Loiro");
                coloresBrasil = Collections.unmodifiableMap(aMap);
        }

        private Hashtable<Long, Meli> hashMelis = new Hashtable<>();

        @Autowired
        ResultadoPublicacionRepositoryM resultadoPublicacionRepositoryM;

        @Autowired
        ResultadoPublicacionProductoRepositoryM resultadoPublicacionProductoRepositoryM;

        @Override
        public void getResultPublication(SiteDto site, List<CredentialsSiteChannelDto> credentials,
                        ProcesoInfo procesoInfo,
                        CanalSiteBD cmCanalSite, String jobId, List<String> params) throws Exception {
                procesoInfo.addDescripcion("getProducts INIT");
                List<ResultadoPublicacionProductoM> idsNoChm = new ArrayList<ResultadoPublicacionProductoM>();
                List<CanalSiteProductoM> idsNoML = new ArrayList<CanalSiteProductoM>();

                ResultadoPublicacionM resultadoPublicacion = procesoInfo.getResultadoPublicacion();
                List<CanalSiteProductoM> listaResultados;
                listaResultados = canalSiteProductoRepositoryM.findAllByIdSiteCanal(cmCanalSite.getIdCanalSite());
                resultadoPublicacion.setCountProducts(new Long(listaResultados.size()));
                procesoInfo.setResultadosTotal(new Long(listaResultados.size()));
                Meli mercado = getMeli(credentials, false, site.getIdSite(), procesoInfo);
                int numeroEnMl = 0;
                // vamos priemro a ver si alguno de los activos no lo tenemos marcado
                List<ResultadoPublicacionProductoM> results = getProducts(credentials, site.getIdSite() + "", mercado,
                                procesoInfo, "");

                // results.addAll(getProducts(credentials, site.getIdSite()+"", mercado,
                // procesoInfo,"&status=inactive"));
                Iterator<ResultadoPublicacionProductoM> iterator = results.iterator();
                StringBuffer str = new StringBuffer("");
                while (iterator.hasNext()) {
                        ResultadoPublicacionProductoM resultado = iterator.next();
                        CanalSiteProductoM ser = listaResultados.stream()
                                        .filter((x) -> x.getIdItemChannel().equals(resultado.getIdItemChannel()))
                                        .findAny()
                                        .orElse(null);
                        numeroEnMl++;
                        if (ser == null) {
                                procesoInfo.addDescripcion("no se ha encontrado el ID=" + resultado.getIdItemChannel()
                                                + " en el chm");
                                str.append("no se ha encontrado el ID=" + resultado.getIdItemChannel() + " en el chm");
                                idsNoChm.add(resultado);

                                // vamos a buscar el modelo
                                ProductoCollectionDto pr = hermesCore.getDetallesProductoByIdArticuloModalia(
                                                site.getIdSiteKiosco(), resultado.getSkuHermesChannel());
                                if (pr == null || pr.getIdProducto() == null) {
                                        List<ProductoCollectionDto> list = hermesCore.leerProductoByCodigoAlfaAndIdSite(
                                                        resultado.getSkuHermesChannel(), site.getIdSiteKiosco());
                                        if (list != null && list.size() > 0) {
                                                pr = list.get(0);
                                        }
                                }
                                if (pr == null) {
                                        procesoInfo.addDescripcion("no se ha encontrado el modelo="
                                                        + resultado.getSkuHermesChannel() + " en el chm");
                                        str.append("no se ha encontrado el modelo=" + resultado.getSkuHermesChannel()
                                                        + " en el chm");
                                } else {
                                        procesoInfo.addDescripcion("actualizamos iditemchannel para el producto="
                                                        + pr.getIdProducto());
                                        str.append("actualizamos iditemchannel para el producto=" + pr.getIdProducto());
                                        final Long idProducto = pr.getIdProducto();
                                        CanalSiteProductoM serPro = listaResultados.stream()
                                                        .filter((x) -> x.getIdProducto().equals(idProducto))
                                                        .findAny()
                                                        .orElse(null);
                                        if (serPro != null) {
                                                serPro.setIdItemChannel(resultado.getIdItemChannel());
                                                serPro.setPublicado(true);
                                                canalSiteProductoRepositoryM.save(serPro);
                                        } else {
                                                CanalSiteProductoM canalSiteProductoUpdate = new CanalSiteProductoM(
                                                                null,
                                                                cmCanalSite.getIdCanalSite(),
                                                                pr.getIdProducto(),
                                                                resultado.getIdItemChannel(),
                                                                true,
                                                                pr.getPrecio() + "",
                                                                pr.getPrecioRebajado() + "",
                                                                "",
                                                                "",
                                                                procesoInfo.getResultadoPublicacion()
                                                                                .getIdPublicacion());
                                                canalSiteProductoRepositoryM.insert(canalSiteProductoUpdate);
                                        }
                                }

                        } else {
                                if (resultado.getSkuHermesChannel() == null) {
                                        procesoInfo.addDescripcion("NO se ha encontrado MODELO para el ID="
                                                        + resultado.getIdItemChannel() + " de ML");
                                        str.append("NO se ha encontrado MODELO para el ID="
                                                        + resultado.getIdItemChannel() + " de ML");
                                } else {
                                        ProductoCollectionDto pr = hermesCore.getProductoById(ser.getIdProducto());
                                        if (!pr.getCodigoAlfa().equals(resultado.getSkuHermesChannel())) {
                                                procesoInfo.addDescripcion("INCOHERENCIA DE MODELO para el ID="
                                                                + resultado.getIdItemChannel()
                                                                + " de ML. modelo en hermes " + pr.getCodigoAlfa()
                                                                + " . Modelo en ML " + resultado.getSkuHermesChannel());
                                                str.append("INCOHERENCIA DE MODELO para el ID="
                                                                + resultado.getIdItemChannel()
                                                                + " de ML. modelo en hermes " + pr.getCodigoAlfa()
                                                                + " . Modelo en ML " + resultado.getSkuHermesChannel());

                                        }
                                }
                        }
                }
                if (!str.toString().isEmpty()) {
                        mailSenderService.createMailSender(
                                        new String[] { "monitorizacionhermes@moddo.com" },
                                        "ATENCION - errores ML PUBLICACION en site " + site.getNombre() + "- ATENCION",
                                        str.toString());

                }

                procesoInfo.addDescripcion("errores=" + str.toString());
                procesoInfo.addDescripcion("tenemos " + numeroEnMl + " productos publicados en ML");
                for (CanalSiteProductoM idNoML : idsNoML) {
                        procesoInfo.addDescripcion("no se ha encontrado el ID=" + idNoML.getIdItemChannel()
                                        + " en ML o no esta activo y si lo tenemos como publicado");
                }
                for (ResultadoPublicacionProductoM idNoML : idsNoChm) {
                        procesoInfo.addDescripcion(
                                        "no se ha encontrado el ID=" + idNoML.getIdItemChannel() + " en el chm");
                }

                procesoInfo.setResultadoPublicacion(resultadoPublicacion);
                // procesoInfo.addDescripcion("insertItems END");
                return;
        }

        @Override
        public void updateProducts(SiteDto site, List<CredentialsSiteChannelDto> credentials,
                        List<ProductoCollectionDto> productos, ProcesoInfo procesoInfo, CanalSiteBD cmCanalSite)
                        throws Exception {

                procesoInfo.addDescripcion("updateItems INIT");
                procesoInfo.addDescripcion("Products read from publications: " + productos.size());
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                ResultadoPublicacionM resultadoPublicacion = (ResultadoPublicacionM) procesoInfo
                                .getResultadoPublicacion();
                resultadoPublicacion.setCountProducts(new Long(productos.size()));
                procesoInfo.setResultadosTotal(new Long(productos.size()));
                int total = productos.size();
                int buenos = 0;

                Meli mercado = getMeli(credentials, false, site.getIdSite(), procesoInfo);

                String[] headerQueryParams = { "Accept" };
                String[] headerQueryParamsValues = { "application/json" };

                procesoInfo.addDescripcion("products=" + productos.size());

                List<Long> idProducts = new ArrayList<>();

                // insertamos
                List<AlmacenDto> almacenes = hermesCore.findTiendasClickCollect(site.getIdSite());

                if (productos.size() > 0) {
                        Hashtable<String, List<String>> hashGuias = new Hashtable<>();
                        Boolean algunaTalla = false;
                        for (ProductoCollectionDto producto : productos) {

                                ResultadoPublicacionProductoM resultDetalle = new ResultadoPublicacionProductoM();
                                resultadoPublicacion.getProducts().add(resultDetalle);
                                resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                resultDetalle.setIdProducto(producto.getIdProducto());
                                resultDetalle.setAlphaCode(producto.getCodigoAlfa());
                                resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
                                try {
                                        String productIdML = getIdProductoML(site, cmCanalSite, producto);
                                        CanalSiteProductoM canalSiteProducto = buscarProducto(producto.getIdProducto(),
                                                        cmCanalSite);
                                        if (canalSiteProducto == null || !canalSiteProducto.getPublicado()) {
                                                continue;
                                        }
                                        resultDetalle = actualizarIdML(producto, site, productIdML, mercado,
                                                        credentials, procesoInfo);
                                        List<ResultadoPublicacionProductoVariantM> variantes = resultDetalle
                                                        .getVariants();

                                        // if (true) continue;
                                        // buscamos guia por los tres parametros
                                        String guia = env.getProperty("mercado.libre.guia." + site.getIdSite() + "."
                                                        + producto.getTipoProducto() + "." + producto.getIdMarca() + "."
                                                        + producto.getGenero());
                                        log.info("mercado.libre.guia." + site.getIdSite() + "."
                                                        + producto.getTipoProducto() + "." + producto.getIdMarca() + "."
                                                        + producto.getGenero());
                                        if (StringUtils.isEmpty(guia)) {
                                                guia = env.getProperty("mercado.libre.guia." + site.getIdSite() + "."
                                                                + producto.getTipoProducto() + "."
                                                                + producto.getGenero());
                                                if (StringUtils.isEmpty(guia)) {
                                                        guia = env.getProperty("mercado.libre.guia." + site.getIdSite()
                                                                        + "." + producto.getIdMarca());
                                                }
                                        }
                                        log.info("mercado.libre.guia." + site.getIdSite() + "."
                                                        + producto.getTipoProducto() + "." + producto.getGenero());
                                        procesoInfo.addDescripcion("guia=" + guia);
                                        if (!StringUtils.isEmpty(guia)) {
                                                List<String> productosHash = hashGuias.get(guia);
                                                if (productosHash == null) {
                                                        productosHash = new ArrayList<>();
                                                }
                                                productosHash.add(canalSiteProducto.getIdItemChannel());
                                                hashGuias.put(guia, productosHash);
                                                algunaTalla = true;
                                        }
                                        String garantia = TypeTools.getCredential(credentials,
                                                        Credenciales.MERCADO_GARANTIA);
                                        resultDetalle = updateItemsInternal(cmCanalSite, producto,
                                                        site.getIdSite().toString(), site, true, productIdML, mercado,
                                                        garantia, credentials, resultDetalle, procesoInfo, almacenes);
                                        if (!resultDetalle.isOk()) {
                                                resultDetalle = updateItemsInternalOnlyCategoria(cmCanalSite, producto,
                                                                site.getIdSite().toString(), site, true, productIdML,
                                                                mercado, garantia, credentials, resultDetalle,
                                                                procesoInfo, almacenes);
                                        }
                                        if (resultDetalle.isOk()) {
                                                // actualizamos sub productos
                                                variantes = resultDetalle.getVariants();
                                                if (variantes != null && variantes.size() > 0) {
                                                        for (ResultadoPublicacionProductoVariantM variante : variantes) {
                                                                if (!StringUtils.isEmpty(variante.getIdItemChannel())) {
                                                                        procesoInfo.addDescripcion("variant="
                                                                                        + BeanDescriber.stringify(
                                                                                                        variante));
                                                                        SubProductoDto sb = hermesCore
                                                                                        .leerSubProductoPorIdSubProducto(
                                                                                                        variante.getIdSubProducto());
                                                                        if (StringUtils.isEmpty(
                                                                                        sb.getAtributoStr("ID_ITEM_ML"))
                                                                                        && !StringUtils.isEmpty(variante
                                                                                                        .getIdItemChannel())) {
                                                                                sb.setAtributo("ID_ITEM_ML", variante
                                                                                                .getIdItemChannel());
                                                                                // variante.setStock(sb.getStockEnSite()+"");
                                                                                variante.setEan(sb.getEan());
                                                                                variante.setSize(sb
                                                                                                .getTallajeOriginalCliente());
                                                                                variante.setSkuHermesChannel("" + sb
                                                                                                .getIdSubProducto());
                                                                                variante.setOk(true);
                                                                                variante.setError("");
                                                                                hermesCore.actualizarSubProductoAtributos(
                                                                                                sb.getIdSubProducto(),
                                                                                                JSONObject.fromObject(sb
                                                                                                                .getAtributos()));
                                                                        }
                                                                }
                                                        }
                                                }
                                                buenos++;
                                        }
                                        procesoInfo.addDescripcion(getProductNumber(producto) + " updated");
                                        resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                        publicationResultService.guardarResultadoProductoPositivo(resultDetalle,
                                                        canalSiteProducto);
                                        resultadoPublicacion
                                                        .setCountProducts(resultadoPublicacion.getCountProducts() + 1);

                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("Error mercadolibre: ", e);
                                        resultDetalle.setOk(false);
                                        resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                        resultDetalle.setError(e.toString());
                                        publicationResultService.guardarResultadoProducto(resultDetalle, null,
                                                        procesoInfo);
                                        monitor.alert(cmCanalSite.getIdCanal().getIdCanal(),
                                                        cmCanalSite.getIdSiteRotulo(), "UpdateProducts");
                                }
                        }
                        try {
                                if (algunaTalla) {

                                        FluentStringsMap params = new FluentStringsMap();
                                        params.add("access_token", mercado.getAccessToken());
                                        params.add("limit", "5000");
                                        Enumeration e = hashGuias.keys();
                                        String clave;
                                        List<String> valor;
                                        while (e.hasMoreElements()) {
                                                clave = (String) e.nextElement();
                                                valor = hashGuias.get(clave);

                                                String[] queryParams = { "access_token", "limit" };
                                                String[] queryParamsValues = { mercado.getAccessToken(), "5000" };

                                                String respuesta = mercado.httpGet("/size_charts/" + clave + "/items",
                                                                queryParams, queryParamsValues, headerQueryParams,
                                                                headerQueryParamsValues);
                                                procesoInfo.addDescripcion("respuesta productos para talla=" + clave
                                                                + ": " + respuesta);
                                                JSONObject jObject = JSONObject.fromObject(respuesta); // json*/

                                                JSONArray array = jObject.getJSONArray("items");
                                                for (int i = 0; i < array.size(); i++) {
                                                        String oVariante = (String) array.get(i);
                                                        if (!valor.contains(oVariante)) {
                                                                valor.add(oVariante);
                                                        }
                                                }
                                        }
                                        params = new FluentStringsMap();
                                        params.add("access_token", mercado.getAccessToken());
                                        e = hashGuias.keys();
                                        clave = null;
                                        valor = null;
                                        while (e.hasMoreElements()) {
                                                clave = (String) e.nextElement();
                                                valor = hashGuias.get(clave);
                                                JSONArray imagenes = new JSONArray();
                                                for (String pr : valor) {
                                                        imagenes.add(pr);
                                                }

                                                JSONObject productoJson = new JSONObject();
                                                productoJson.put("items", imagenes);

                                                Response r = mercado.put("/size_charts/" + clave + "/items", params,
                                                                productoJson.toString());
                                                procesoInfo.addDescripcion("respuesta tallas: " + r.getResponseBody());
                                        }
                                }
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("error respuesta tallas: " + e);
                                monitor.alert(cmCanalSite.getIdCanal().getIdCanal(), cmCanalSite.getIdSiteRotulo(),
                                                "UpdateProducts");
                        }

                }
                procesoInfo.addDescripcion("endDate " + sdf.format(new Date()));
                procesoInfo.setResultadosOk(new Long(buenos));
                procesoInfo.addDescripcion("Updated " + buenos + " of " + total);
                procesoInfo.setResultadoPublicacion(resultadoPublicacion);
                procesoInfo.addDescripcion("updateItems END");
                monitor.event(cmCanalSite.getIdCanal().getIdCanal(), cmCanalSite.getIdSiteRotulo(), "UpdateProducts");
                return;
        }

        @Override
        public void updatePrices(SiteDto site, List<CredentialsSiteChannelDto> credentials,
                        List<ProductoCollectionDto> productos, ProcesoInfo procesoInfo, CanalSiteBD cmCanalSite)
                        throws Exception {
                procesoInfo.addDescripcion("updatePrices INIT");
                procesoInfo.addDescripcion("Products read from publications: " + productos.size());
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                ResultadoPublicacionM resultadoPublicacion = procesoInfo.getResultadoPublicacion();
                resultadoPublicacion.setCountProducts(new Long(productos.size()));

                int total = productos.size();
                int buenos = 0;

                Meli mercado = getMeli(credentials, false, site.getIdSite(), procesoInfo);
                String[] headerQueryParams = { "Accept" };
                String[] headerQueryParamsValues = { "application/json" };
                String idOferta = TypeTools.getCredential(credentials, Credenciales.MERCADO_OFERTA);
                String sellerId = TypeTools.getCredential(credentials, Credenciales.MERCADO_LIBRE_ORDERID);
                List<String> itemsEnOfertas = new ArrayList<>();
                procesoInfo.addDescripcion("buscamos en oferta=" + idOferta);
                if (!StringUtils.isEmpty(idOferta)) {
                        try {
                                int pagina = 1;
                                procesoInfo.addDescripcion("pagina=" + pagina);
                                FluentStringsMap params = new FluentStringsMap();
                                params.add("access_token", mercado.getAccessToken());
                                params.add("limit", "6000");
                                params.add("offset", pagina + "");
                                procesoInfo.addDescripcion(
                                                "/users/" + sellerId + "/deals/" + idOferta + "/proposed_items/search");

                                String[] queryParams = { "access_token", "limit", "offset" };
                                String[] queryParamsValues = { mercado.getAccessToken(), "6000", pagina + "" };

                                String respuesta = mercado.httpGet(
                                                "/seller-promotions/promotions/" + idOferta
                                                                + "/items?promotion_type=DEAL",
                                                queryParams, queryParamsValues, headerQueryParams,
                                                headerQueryParamsValues);
                                procesoInfo.addDescripcion(respuesta);
                                JSONObject jObject = JSONObject.fromObject(respuesta); // json*/

                                /*
                                 * Response rDeals=mercado.get("/users/"+sellerId+"/deals/"+idOferta+
                                 * "/proposed_items/search", params);
                                 * procesoInfo.addDescripcion(rDeals.getResponseBody());
                                 * JSONObject jObject = JSONObject.fromObject(rDeals.getResponseBody()); // json
                                 */

                                JSONArray tiendas = jObject.getJSONArray("results");
                                for (int j = 0; j < tiendas.size(); j++) {
                                        JSONObject tienda = (JSONObject) tiendas.get(j);
                                        itemsEnOfertas.add(tienda.getString("item_id"));
                                }
                                pagina++;
                                /*
                                 * }catch (Exception e) {
                                 * procesoInfo.addDescripcion("",e);
                                 * tienePedidos=false;
                                 * }
                                 * }
                                 */
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                        }
                }

                List<ModeloAtributoBD> atributosModeloStock = stockModelServiceImpl
                                .leerModeloStock(procesoInfo.getResultadoPublicacion().getIdPublicacion());

                // insertamos
                int contador = 0;
                String SITE_ML = TypeTools.getCredential(credentials, Credenciales.MERCADO_MELI_COUNTRY_CODE);
                if (productos.size() > 0) {

                        for (ProductoCollectionDto producto : productos) {
                                contador++;
                                mercado = getMeli(credentials, false, site.getIdSite(), procesoInfo);
                                ResultadoPublicacionProductoM resultDetalle = new ResultadoPublicacionProductoM();
                                resultDetalle.setIdProducto(producto.getIdProducto());
                                resultDetalle.setAlphaCode(producto.getCodigoAlfa());
                                resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
                                try {
                                        String productIdML = getIdProductoML(site, cmCanalSite, producto);

                                        procesoInfo.addDescripcion("Preparing product ID " + producto.getIdProducto()
                                                        + " ML=" + productIdML + " (" + contador + "/"
                                                        + productos.size() + ")");
                                        CanalSiteProductoM canalSiteProducto = buscarProducto(producto.getIdProducto(),
                                                        cmCanalSite);
                                        // TODO mirar a ver si aqui va a haber que mandar un 0 si no esta activo
                                        if (productIdML == null || canalSiteProducto == null
                                                        || !canalSiteProducto.getPublicado()) {
                                                resultDetalle.setIdProducto(producto.getIdProducto());
                                                resultDetalle.setAlphaCode(producto.getCodigoAlfa());
                                                resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                                resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
                                                resultDetalle.setOk(false);
                                                resultDetalle.setError("Product not published or inactive");
                                                publicationResultService.guardarResultadoProductoPositivo(resultDetalle,
                                                                canalSiteProducto);
                                                resultadoPublicacion.setCountProducts(
                                                                resultadoPublicacion.getCountProducts() + 1);
                                                continue;
                                        }
                                        Boolean enOferta = false;
                                        if (itemsEnOfertas.contains(productIdML)) {
                                                enOferta = true;
                                        }
                                        resultadoPublicacion.getProducts().add(resultDetalle);

                                        ProductoCollectionDto productoActualizado = stockModelServiceImpl
                                                        .leerProductoActualizado(site, producto, atributosModeloStock,
                                                                        procesoInfo,
                                                                        resultadoPublicacion.getIdPublicacion());
                                        producto.setSubProductos(productoActualizado.getSubProductos());

                                        resultDetalle = updatePricesInternal(cmCanalSite, producto, site,
                                                        productIdML, mercado, idOferta, credentials, resultDetalle,
                                                        procesoInfo, enOferta, SITE_ML);
                                        procesoInfo.addDescripcion("Completed product ID " + producto.getIdProducto()
                                                        + " (" + contador + "/" + productos.size() + ")");
                                        resultDetalle.setIdProducto(producto.getIdProducto());
                                        resultDetalle.setAlphaCode(producto.getCodigoAlfa());
                                        resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                        resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());

                                        // actualizamos sub productos
                                        List<ResultadoPublicacionProductoVariantM> variantes = resultDetalle
                                                        .getVariants();
                                        for (ResultadoPublicacionProductoVariantM variante : variantes) {
                                                procesoInfo.addDescripcion(
                                                                "variante=" + BeanDescriber.stringify(variante));

                                                if (!StringUtils.isEmpty(variante.getIdItemChannel())) {
                                                        SubProductoDto sb = hermesCore.leerSubProductoPorIdSubProducto(
                                                                        variante.getIdSubProducto());
                                                        variante.setStock(sb.getStockEnSite() + "");
                                                        variante.setEan(sb.getEan());
                                                        variante.setSize(sb.getTallajeOriginalCliente());
                                                        variante.setSkuHermesChannel("" + sb.getIdSubProducto());
                                                        variante.setOk(true);
                                                        variante.setError("");
                                                }
                                        }
                                        publicationResultService.guardarResultadoProductoYHash(resultDetalle,
                                                        canalSiteProducto, procesoInfo, producto,
                                                        cmCanalSite.getIdSiteCanal());
                                        resultadoPublicacion
                                                        .setCountProducts(resultadoPublicacion.getCountProducts() + 1);

                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("Error mercadolibre: ", e);
                                        resultDetalle.setOk(false);
                                        resultDetalle.setError(e.toString());
                                        publicationResultService.guardarResultadoProducto(resultDetalle, null,
                                                        procesoInfo);
                                        monitor.alert(cmCanalSite.getIdCanal().getIdCanal(),
                                                        cmCanalSite.getIdSiteRotulo(), "UpdatePrices");
                                }
                        }

                }
                procesoInfo.addDescripcion("endDate " + sdf.format(new Date()));
                procesoInfo.setResultadosOk(new Long(buenos));
                procesoInfo.addDescripcion("Updated " + buenos + " of " + total);
                procesoInfo.setResultadoPublicacion(resultadoPublicacion);
                procesoInfo.addDescripcion("updatePrices END");
                monitor.event(cmCanalSite.getIdCanal().getIdCanal(), cmCanalSite.getIdSiteRotulo(), "UpdatePrices");
                return;
        }

        @Override
        public void createProducts(SiteDto site, List<CredentialsSiteChannelDto> credentials,
                        List<ProductoCollectionDto> productosLocalizadosConStock, ProcesoInfo procesoInfo,
                        CanalSiteBD cmCanalSite) throws Exception {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                procesoInfo.addDescripcion("createProducts INIT");
                procesoInfo.addDescripcion("Products read from publications: " + productosLocalizadosConStock.size());

                ResultadoPublicacionM resultadoPublicacion = (ResultadoPublicacionM) procesoInfo
                                .getResultadoPublicacion();
                resultadoPublicacion.setCountProducts(new Long(productosLocalizadosConStock.size()));
                procesoInfo.setResultadosTotal(new Long(productosLocalizadosConStock.size()));
                int buenos = 0;
                int total = 0;

                // Meli mercado = getMeli(credentials, false, site.getIdSite(), procesoInfo);
                CencosudProvider cencosudProvider = new CencosudProvider();

                // insertamos
                String garantia = TypeTools.getCredential(credentials, Credenciales.MERCADO_GARANTIA);
                int contador = 1;
                if (productosLocalizadosConStock.size() > 0) {
                        for (ProductoCollectionDto producto : productosLocalizadosConStock) {
                                ResultadoPublicacionProductoM resultDetalle = new ResultadoPublicacionProductoM();
                                resultDetalle.setIdProducto(producto.getIdProducto());
                                resultDetalle.setAlphaCode(producto.getCodigoAlfa());
                                resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
                                try {
                                        procesoInfo.addDescripcion("Preparing product ID " + producto.getIdProducto()
                                                        + " (" + contador + "/" + productosLocalizadosConStock.size()
                                                        + ")");
                                        CanalSiteProductoM canalSiteProducto = buscarProducto(producto.getIdProducto(),
                                                        cmCanalSite);
                                        if (canalSiteProducto != null && canalSiteProducto.getPublicado()) {
                                                continue;
                                        }
                                        total++;
                                        resultadoPublicacion.getProducts().add(resultDetalle);
                                        resultDetalle = insertItemsInternal(cmCanalSite, credentials, producto,
                                                        "" + site.getIdSite(), site, true, null, mercado, garantia,
                                                        procesoInfo, resultDetalle);

                                        procesoInfo.addDescripcion("Completed product ID " + producto.getIdProducto()
                                                        + " (" + contador + "/" + productosLocalizadosConStock.size()
                                                        + ")");

                                        if (resultDetalle.isOk() && resultDetalle.getIdItemChannel() != null) {
                                                resultDetalle.setIdItemChannel(resultDetalle.getIdItemChannel());
                                                resultDetalle.setOk(true);
                                                resultDetalle.setError("");
                                                // actualizamos sub productos
                                                List<ResultadoPublicacionProductoVariantM> variantes = resultDetalle
                                                                .getVariants();
                                                for (ResultadoPublicacionProductoVariantM variante : variantes) {

                                                        if (!StringUtils.isEmpty(variante.getIdItemChannel())) {
                                                                SubProductoDto sb = hermesCore
                                                                                .leerSubProductoPorIdSubProducto(
                                                                                                variante.getIdSubProducto());
                                                                sb.setAtributo("ID_ITEM_ML",
                                                                                variante.getIdItemChannel());
                                                                hermesCore.actualizarSubProductoAtributos(
                                                                                sb.getIdSubProducto(),
                                                                                JSONObject.fromObject(
                                                                                                sb.getAtributos()));
                                                                variante.setStock(sb.getStockEnSite() + "");
                                                                variante.setEan(sb.getEan());
                                                                variante.setSize(sb.getTallajeOriginalCliente());
                                                                variante.setSkuHermesChannel(
                                                                                "" + sb.getIdSubProducto());
                                                                variante.setOk(true);
                                                                variante.setError("");
                                                        }
                                                }

                                                // Meter el producto en la colección CANAL_SITE_PRODUCTO con los datos
                                                // actualizados
                                                CanalSiteProductoM canalSiteProductoUpdate = new CanalSiteProductoM(
                                                                canalSiteProducto != null ? canalSiteProducto.get_id()
                                                                                : null,
                                                                cmCanalSite.getIdCanalSite(),
                                                                producto.getIdProducto(),
                                                                resultDetalle.getIdItemChannel(),
                                                                true,
                                                                producto.getPrecio() + "",
                                                                producto.getPrecioRebajado() + "",
                                                                "",
                                                                "",
                                                                procesoInfo.getResultadoPublicacion()
                                                                                .getIdPublicacion());

                                                publicationResultService.guardarResultadoProductoPositivo(resultDetalle,
                                                                canalSiteProductoUpdate);
                                        } else {
                                                resultDetalle.setOk(false);
                                                publicationResultService.guardarResultadoProducto(resultDetalle, null,
                                                                procesoInfo);
                                        }

                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                        resultDetalle.setError(e.toString());
                                        resultDetalle.setOk(false);
                                        publicationResultService.guardarResultadoProducto(resultDetalle, null,
                                                        procesoInfo);
                                        monitor.alert(cmCanalSite.getIdCanal().getIdCanal(),
                                                        cmCanalSite.getIdSiteRotulo(), "CreateProducts");
                                }
                        }
                }
                procesoInfo.addDescripcion("endDate " + sdf.format(new Date()));
                procesoInfo.setResultadosOk(new Long(buenos));
                procesoInfo.addDescripcion("Created " + buenos + " of " + total);
                procesoInfo.setResultadoPublicacion(resultadoPublicacion);
                procesoInfo.addDescripcion("createProducts END");
                monitor.event(cmCanalSite.getIdCanal().getIdCanal(), cmCanalSite.getIdSiteRotulo(), "CreateProducts");
                return;
        }

        @Override
        /**
         * Metodo para actualizar stock masivo. la ejecucion no termina hasta que
         * mercadolibre da un OK
         */
        public void updateStocks(SiteDto site, List<CredentialsSiteChannelDto> credentials,
                        List<ProductoCollectionDto> productos, ProcesoInfo procesoInfo, CanalSiteBD cmCanalSite)
                        throws Exception {
                procesoInfo.addDescripcion("updateStock INIT");

                procesoInfo.addDescripcion("Products read from publications: " + productos.size());
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                ResultadoPublicacionM resultadoPublicacion = procesoInfo.getResultadoPublicacion();
                resultadoPublicacion.setCountProducts(new Long(productos.size()));

                int total = productos.size();
                int buenos = 0;
                String SITE_ML = TypeTools.getCredential(credentials, Credenciales.MERCADO_MELI_COUNTRY_CODE);

                Meli mercado = getMeli(credentials, false, site.getIdSite(), procesoInfo);

                // insertamos
                int contador = 0;
                List<ModeloAtributoBD> atributosModeloStock = stockModelServiceImpl
                                .leerModeloStock(procesoInfo.getResultadoPublicacion().getIdPublicacion());
                if (productos.size() > 0) {
                        for (ProductoCollectionDto producto : productos) {
                                procesoInfo.addDescripcion("HashProducto=" + producto.getHashCodeProductoPorCanal());
                                procesoInfo.addDescripcion("updateStock INIT");
                                contador++;
                                mercado = getMeli(credentials, false, site.getIdSite(), procesoInfo);
                                ResultadoPublicacionProductoM resultDetalle = new ResultadoPublicacionProductoM();
                                resultDetalle.setIdProducto(producto.getIdProducto());
                                resultDetalle.setAlphaCode(producto.getCodigoAlfa());
                                resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
                                try {
                                        String productIdML = getIdProductoML(site, cmCanalSite, producto);

                                        procesoInfo.addDescripcion("Preparing product ID " + producto.getIdProducto()
                                                        + " ML=" + productIdML + " (" + contador + "/"
                                                        + productos.size() + ")");
                                        CanalSiteProductoM canalSiteProducto = buscarProducto(producto.getIdProducto(),
                                                        cmCanalSite);
                                        // TODO mirar a ver si aqui va a haber que mandar un 0 si no esta activo
                                        if (productIdML == null || canalSiteProducto == null
                                                        || !canalSiteProducto.getPublicado()) {
                                                resultDetalle.setIdProducto(producto.getIdProducto());
                                                resultDetalle.setAlphaCode(producto.getCodigoAlfa());
                                                resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                                resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
                                                resultDetalle.setOk(false);
                                                resultDetalle.setError("Product not published or inactive");
                                                publicationResultService.guardarResultadoProductoPositivo(resultDetalle,
                                                                canalSiteProducto);
                                                resultadoPublicacion.setCountProducts(
                                                                resultadoPublicacion.getCountProducts() + 1);
                                                continue;
                                        }

                                        resultadoPublicacion.getProducts().add(resultDetalle);

                                        ProductoCollectionDto productoActualizado = stockModelServiceImpl
                                                        .leerProductoActualizado(site, producto, atributosModeloStock,
                                                                        procesoInfo,
                                                                        resultadoPublicacion.getIdPublicacion());
                                        producto.setSubProductos(productoActualizado.getSubProductos());

                                        List<CategoriaCanalAtributoBD> attributes = getAttributes(mercado,
                                                        cmCanalSite.getIdSiteRotulo(),
                                                        producto.getIdCategorias(), procesoInfo);
                                        ;

                                        resultDetalle = updateVariantesInternal(producto, attributes, site, productIdML,
                                                        mercado, procesoInfo,
                                                        resultDetalle, false, SITE_ML, credentials);

                                        if (!resultDetalle.isOk()) {
                                                procesoInfo.addDescripcion("Tenemos error ene l producto="
                                                                + producto.getIdProducto()
                                                                + ", vamos a actualizar ids y reintentamos");
                                                resultDetalle = actualizarIdML(producto, site, productIdML, mercado,
                                                                credentials, procesoInfo);
                                                resultDetalle = updateVariantesInternal(producto, attributes, site,
                                                                productIdML, mercado,
                                                                procesoInfo, resultDetalle, false, SITE_ML,
                                                                credentials);
                                        }

                                        procesoInfo.addDescripcion("Completed product ID " + producto.getIdProducto()
                                                        + " (" + contador + "/" + productos.size() + ")");
                                        resultDetalle.setIdProducto(producto.getIdProducto());
                                        resultDetalle.setAlphaCode(producto.getCodigoAlfa());
                                        resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                        resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());

                                        List<ResultadoPublicacionProductoVariantM> variantes = resultDetalle
                                                        .getVariants();
                                        for (ResultadoPublicacionProductoVariantM variante : variantes) {
                                                // procesoInfo.addDescripcion("variante=" +
                                                // BeanDescriber.stringify(variante));

                                                if (!StringUtils.isEmpty(variante.getIdItemChannel())) {
                                                        SubProductoDto sb = hermesCore.leerSubProductoPorIdSubProducto(
                                                                        variante.getIdSubProducto());

                                                        variante.setEan(sb.getEan());
                                                        variante.setSize(sb.getTallajeOriginalCliente());
                                                        variante.setSkuHermesChannel("" + sb.getIdSubProducto());
                                                        variante.setOk(true);
                                                        variante.setError("");
                                                }
                                        }

                                        publicationResultService.guardarResultadoProductoYHash(resultDetalle,
                                                        canalSiteProducto, procesoInfo, producto,
                                                        cmCanalSite.getIdSiteCanal());
                                        resultadoPublicacion
                                                        .setCountProducts(resultadoPublicacion.getCountProducts() + 1);

                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                        procesoInfo.addDescripcion("Error mercadolibre: ", e);
                                        resultDetalle.setOk(false);
                                        resultDetalle.setError(e.toString());
                                        publicationResultService.guardarResultadoProducto(resultDetalle, null,
                                                        procesoInfo);
                                        monitor.alert(cmCanalSite.getIdCanal().getIdCanal(),
                                                        cmCanalSite.getIdSiteRotulo(), "UpdateStocks");
                                }
                        }

                }
                procesoInfo.addDescripcion("endDate " + sdf.format(new Date()));
                procesoInfo.setResultadosOk(new Long(buenos));
                procesoInfo.addDescripcion("Updated " + buenos + " of " + total);
                procesoInfo.setResultadoPublicacion(resultadoPublicacion);
                procesoInfo.addDescripcion("updateStock END");
                monitor.event(cmCanalSite.getIdCanal().getIdCanal(), cmCanalSite.getIdSiteRotulo(), "UpdateStocks");
                return;
        }

        public String getOrder(List<CredentialsSiteChannelDto> credentials, Meli mercado, String idOrder,
                        ProcesoInfo procesoInfo) throws Exception {
                String result = "";
                // https://api.mercadolibre.com/orders/$ORDER_ID?MERCADO_AUTH_TOKEN=$MERCADO_AUTH_TOKEN
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("seller", TypeTools.getCredential(credentials, Credenciales.MERCADO_LIBRE_ORDERID));
                params.put("access_token", mercado.getAccessToken());
                procesoInfo.addDescripcion("seller: " + mercado.getAccessToken());
                result = HttpClientTools.invocarGetConRespuesta("https://api.mercadolibre.com/orders/" + idOrder,
                                params);
                procesoInfo.addDescripcion("getOrder: " + result);
                return result;
        }

        public String getPickupInfo(List<CredentialsSiteChannelDto> credentials, Meli mercado, long idPickup,
                        ProcesoInfo procesoInfo) throws Exception {
                String result = "";
                // https://api.mercadolibre.com/shipments/shipment_id?access_token=$ACCESS_TOKEN
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("access_token", mercado.getAccessToken());
                procesoInfo.addDescripcion("seller: " + mercado.getAccessToken());
                result = HttpClientTools.invocarGetConRespuesta("https://api.mercadolibre.com/pickups/" + idPickup,
                                params);
                procesoInfo.addDescripcion("getPickupInfo: " + result);
                return result;
        }

        @Override
        public void importOrders(SiteDto site, List<CredentialsSiteChannelDto> credentials, ProcesoInfo procesoInfo,
                        CanalSiteBD cmCanalSite) throws Exception {
                procesoInfo.addDescripcion("getOrders START");
                ResultadoPublicacionM resultadoPublicacion = (ResultadoPublicacionM) procesoInfo
                                .getResultadoPublicacion();
                resultadoPublicacion.setCountError(0L);
                List<String> packIdsTratados = new ArrayList<>();
                String SITE_ML = TypeTools.getCredential(credentials, Credenciales.MERCADO_MELI_COUNTRY_CODE);

                JSONObject jsonObject = new JSONObject();
                DecimalFormat fNumero = new DecimalFormat("#0.00");
                Meli mercado = getMeli(credentials, false, site.getIdSite(), procesoInfo);
                List<AlmacenDto> almacenes = hermesCore.findTiendasClickCollect(site.getIdSite());
                Integer pagina = 0;
                boolean tienePedidos = true;
                Long idSiteProducto = credentials.get(0).getIdSite();
                Long idSiteVenta = credentials.get(0).getIdSiteCanal();
                while (tienePedidos) {
                        JSONObject result = null;
                        try {
                                result = orderMercadoLibreConnector.getOrders(site, credentials, mercado, pagina,
                                                procesoInfo);
                        } catch (Exception e) {
                                procesoInfo.addDescripcion(toString(result));
                                procesoInfo.addDescripcion("", e);
                                hashMelis.remove(site.getIdSite());
                                monitor.alert(cmCanalSite.getIdCanal().getIdCanal(), cmCanalSite.getIdSiteRotulo(),
                                                "ImportOrders");
                                throw new Exception(e);
                        }
                        procesoInfo.addDescripcion(toString(result));
                        pagina++;

                        if (result == null) {
                                tienePedidos = false;
                                procesoInfo.addDescripcion("OK: No pending orders");
                                resultadoPublicacion.setCountOrders(0L);
                                procesoInfo.setResultadoPublicacion(resultadoPublicacion);
                                return;
                        }

                        JSONArray orders = result.getJSONArray("results");
                        if (orders == null || orders.isEmpty()) {
                                tienePedidos = false;
                                jsonObject.element("return", "OK: No orders pending");
                                resultadoPublicacion.setCountOrders(0L);
                                procesoInfo.setResultadoPublicacion(resultadoPublicacion);
                                return;
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                        // result=FileTools.readFile("C:\\proyectos\\plattun-doc\\7_Recursos no
                        // versionados\\INTEGRACIONES\\mercadolibre\\json_orders.json", "UTF-8");
                        procesoInfo.addDescripcion("READ: " + result);
                        Integer nivel = 0;

                        StringBuffer xmlPedidoHermes = null;
                        resultadoPublicacion.setCountOrders(new Long(orders.size()));
                        for (int i = 0; i < orders.size(); i++) {
                                ResultadoPublicacionPedidoM resultadoPedido = new ResultadoPublicacionPedidoM();
                                resultadoPedido.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                try {
                                        JSONObject orderList = orders.getJSONObject(i);

                                        String orderId = orderList.getString("pack_id");
                                        String orderIdOriginal = orderList.getString("id");
                                        String pickupId = orderList.getString("pickup_id");

                                        if (StringUtils.isEmpty(orderId) || "null".equalsIgnoreCase(orderId)) {
                                                orderId = orderIdOriginal;
                                        }

                                        JSONObject order = orderMercadoLibreConnector.getOrder(credentials, mercado,
                                                        orderIdOriginal, procesoInfo);

                                        JSONObject billing_info = orderMercadoLibreConnector.getOrderBillingInfo(
                                                        credentials, mercado, orderIdOriginal, procesoInfo);
                                        if (billing_info == null) {
                                                billing_info = order.getJSONObject("buyer")
                                                                .getJSONObject("billing_info");
                                        }

                                        procesoInfo.addDescripcion("billing_info: " + billing_info);
                                        String nombre = "";
                                        String apellido1 = "";
                                        String dni = "";
                                        String email = "";

                                        if (billing_info != null) {
                                                try {
                                                        JSONArray array = billing_info.getJSONObject("billing_info")
                                                                        .getJSONArray("additional_info");
                                                        for (int itemIndex = 0; itemIndex < array.size(); itemIndex++) {
                                                                JSONObject billing_info_detail = array
                                                                                .getJSONObject(itemIndex);
                                                                String type = (String) billing_info_detail.get("type");
                                                                if (type.equals("FIRST_NAME")) {
                                                                        nombre = (String) billing_info_detail
                                                                                        .get("value");
                                                                }
                                                                if (type.equals("LAST_NAME")) {
                                                                        apellido1 = (String) billing_info_detail
                                                                                        .get("value");
                                                                }
                                                                if (type.equals("DOC_NUMBER")) {
                                                                        dni = (String) billing_info_detail.get("value");
                                                                }
                                                        }
                                                } catch (Exception e) {

                                                }
                                        }
                                        try {
                                                if (StringUtils.isEmpty(nombre)) {
                                                        nombre = order.getJSONObject("buyer").getString("first_name");
                                                }
                                                if (StringUtils.isEmpty(apellido1)) {
                                                        apellido1 = order.getJSONObject("buyer").getString("last_name");
                                                }
                                                if (StringUtils.isEmpty(dni)) {
                                                        dni = billing_info.getJSONObject("billing_info")
                                                                        .getString("doc_number");
                                                }
                                        } catch (Exception e) {

                                        }

                                        if (StringUtils.isEmpty(orderId) || "null".equalsIgnoreCase(orderId)) {
                                                orderId = orderIdOriginal;
                                                JSONArray orderItems = (JSONArray) order.getJSONArray("order_items");
                                                // Meter el id en todos mis articulos
                                                for (int itemIndex = 0; itemIndex < orderItems.size(); itemIndex++) {
                                                        orderItems.getJSONObject(itemIndex).put(
                                                                        MercadoLibreConstants.ATR_ORIGINAL_ORDER_ID,
                                                                        orderIdOriginal);
                                                }
                                        } else {
                                                if (packIdsTratados.contains(orderId)) {
                                                        procesoInfo.addDescripcion(
                                                                        "Skipping duplicate pack_id: " + orderId);
                                                        continue;
                                                }
                                                // Meter el id en todos mis articulos
                                                JSONArray orderItems = (JSONArray) order.getJSONArray("order_items");
                                                for (int itemIndex = 0; itemIndex < orderItems.size(); itemIndex++) {
                                                        orderItems.getJSONObject(itemIndex).put(
                                                                        MercadoLibreConstants.ATR_ORIGINAL_ORDER_ID,
                                                                        orderIdOriginal);
                                                }
                                                // Montar aqui los items de otros pedidos de la misma tanda con el mismo
                                                // pack_id, guardar los ids originales
                                                for (int otherOrders = i + 1; otherOrders < orders
                                                                .size(); otherOrders++) {
                                                        JSONObject orderTmp = orders.getJSONObject(otherOrders);
                                                        if (!StringUtils.isEmpty(orderTmp.getString("pack_id"))
                                                                        && orderTmp.getString("pack_id")
                                                                                        .equals(orderId)) {
                                                                JSONArray orderItemsTmp = (JSONArray) orderTmp
                                                                                .getJSONArray("order_items");
                                                                String orderIdTmp = orderTmp.getString("id");
                                                                for (int itemIndex = 0; itemIndex < orderItemsTmp
                                                                                .size(); itemIndex++) {
                                                                        orderItemsTmp.getJSONObject(itemIndex).put(
                                                                                        MercadoLibreConstants.ATR_ORIGINAL_ORDER_ID,
                                                                                        orderIdTmp);
                                                                }
                                                                order.getJSONArray("order_items").addAll(orderItemsTmp);
                                                        }
                                                }
                                                packIdsTratados.add(orderId);
                                        }
                                        resultadoPedido.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                        procesoInfo.addDescripcion(
                                                        "------------------------------------------ INIT ORDER [T" + i
                                                                        + "]: \n");
                                        procesoInfo.addDescripcion("Transaction [" + i + "]: \n");

                                        resultadoPedido.setOrderNumberChannel(orderId);
                                        // if (!orderId.equals("2589641463")&&!orderId.equals("2589641463")) continue;
                                        procesoInfo.addDescripcion(
                                                        "------------------------------------------ INIT ORDER [T" + i
                                                                        + "]:" + orderId
                                                                        + " \n");
                                        // if (!pedido.contains("1945682468")&&!pedido.contains("1946006313")
                                        // &&!pedido.contains("1947723019")) continue;
                                        xmlPedidoHermes = new StringBuffer();
                                        xmlPedidoHermes.append(
                                                        XmlTools.openTag(nivel, XMLOrderConstants.TAG_SOLICITUDES));
                                        nivel++;
                                        procesoInfo.addDescripcion(
                                                        "------------------------------------------ INIT ORDER [T" + i
                                                                        + "]: \n");
                                        procesoInfo.addDescripcion(BeanDescriber.stringify(order));
                                        // Detalles interesantes
                                        procesoInfo.addDescripcion("++++++++++++++++ Total:");
                                        procesoInfo.addDescripcion(
                                                        BeanDescriber.stringify(order.getDouble("total_amount")));
                                        procesoInfo.addDescripcion("++++++++++++++++ Shipping address:");
                                        procesoInfo.addDescripcion(
                                                        "shipping=" + order.getJSONObject("shipping").toString(4));
                                        procesoInfo.addDescripcion(
                                                        "------------------------------------------ FIN DE ORDEN\n");
                                        procesoInfo.addDescripcion("++++++++++++++++ OrderStatus:");

                                        xmlPedidoHermes.append(
                                                        XmlTools.openTag(nivel, XMLOrderConstants.TAG_SOLICITUD));
                                        nivel++;
                                        xmlPedidoHermes.append(
                                                        XmlTools.printTag(nivel, XMLOrderConstants.TAG_TIPO,
                                                                        XMLOrderConstants.VALUE_VENTA));

                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                        XMLOrderConstants.TAG_NUMERO_PEDIDO_ORIGINAL,
                                                        orderId));

                                        xmlPedidoHermes.append(
                                                        XmlTools.printTag(nivel, XMLOrderConstants.TAG_FECHA_PEDIDO,
                                                                        sdf.format(new Date())));

                                        try {
                                                // JSONObject billing_info =
                                                // order.getJSONObject("buyer").getJSONObject("billing_info");

                                                // String dni = billing_info.getString("doc_number");
                                                if (StringUtils.isNotEmpty(dni)) {
                                                        String valida = TypeTools.getCredential(credentials,
                                                                        Credenciales.MERCADO_RUT);
                                                        if (valida != null && valida.equalsIgnoreCase("SI")) {
                                                                dni = ValidarRut.formatearRUT(dni);
                                                        }
                                                } else {
                                                        dni = "0";
                                                }
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_DNI_ENVIO, dni));


                                                if (StringUtils.isEmpty(nombre)) {
                                                        nombre = "_";
                                                }
                                                if (StringUtils.isEmpty(apellido1)) {
                                                        // Sacar la última palabra del nombre
                                                        procesoInfo.addDescripcion(
                                                                        "APELLIDO 1 ENVIO VACIO, SUSTITUYENDO NOMBRE = "
                                                                                        + nombre);
                                                        Integer idxEspacio = nombre.lastIndexOf(" ");
                                                        apellido1 = nombre.substring(idxEspacio + 1);
                                                        nombre = nombre.substring(0,
                                                                        nombre.length() - apellido1.length()).trim();
                                                        procesoInfo.addDescripcion("APELLIDO 1 ENVIO VACIO, NOMBRE = "
                                                                        + nombre + " APELLIDO=" + apellido1);
                                                }

                                                if (site.isBrasil()) {
                                                        String documentType = billing_info.getJSONObject(
                                                                        "billing_info").has("doc_type") ? billing_info
                                                                                        .getJSONObject("billing_info")
                                                                                        .getString("doc_type") : "";
                                                        if (DOCUMENT_TYPE_CNPJ.equalsIgnoreCase(documentType)) {
                                                                procesoInfo.addDescripcion(
                                                                                "Change flow to brazilian business buyer.");
                                                                JSONArray aditionalInfo = billing_info
                                                                                .getJSONObject("billing_info")
                                                                                .getJSONArray("additional_info");
                                                                nombre = " ⠀⠀⠀⠀⠀⠀⠀⠀⠀";
                                                                apellido1 = " ⠀⠀⠀⠀⠀⠀⠀⠀⠀";
                                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                                "TIPO_IDENTIFICACION",
                                                                                DOCUMENT_TYPE_CNPJ));
                                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                                "RAZON_SOCIAL",
                                                                                this.getAditionalInfoValue(
                                                                                                aditionalInfo,
                                                                                                "BUSINESS_NAME")));
                                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                                "NOMBRE",
                                                                                this.getAditionalInfoValue(
                                                                                                aditionalInfo,
                                                                                                "BUSINESS_NAME")));
                                                                // TODO Waiting for Moddo add new TAG into SOAP Service
                                                                // xmlPedidoHermes.append(XmlTools.printTag(nivel,"STATE_REGISTRATION",
                                                                // this.getAditionalInfoValue(aditionalInfo,
                                                                // "STATE_REGISTRATION")));
                                                        }
                                                }

                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_NOMBRE_ENVIO, nombre));
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_APELLIDO_1_ENVIO, apellido1));
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_APELLIDO_2_ENVIO, ""));
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion(
                                                                "error opteniendo usuario, si tiene id lo buscamos");
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_NOMBRE_ENVIO, "-"));
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_APELLIDO_1_ENVIO, "-"));
                                        }

                                        Boolean hasShippingInfo = order.getJSONObject("shipping")
                                                        .containsKey("receiver_address");
                                        procesoInfo.addDescripcion("SHIPPING " + hasShippingInfo + " : "
                                                        + order.getJSONObject("shipping").toString(4));
                                        Boolean isPickup = !StringUtils.isEmpty(pickupId)
                                                        && !"null".equalsIgnoreCase(pickupId);

                                        JSONObject pickupInfo = null;
                                        String rua = "";
                                        String bairro = "";
                                        String ciudadEnvio = "";
                                        String provinciaS = "";
                                        String codigoPostalEnvio = "";
                                        String observacionesEnvio = "";
                                        String numero = "";
                                        String tipoEnvio = "";
                                        if (isPickup) {
                                                pickupInfo = JSONObject.fromObject(getPickupInfo(credentials, mercado,
                                                                Long.parseLong(pickupId), procesoInfo));
                                                procesoInfo.addDescripcion("STORE PICKUP " + isPickup + " : "
                                                                + pickupInfo.toString(4));
                                                String storeIdML = pickupInfo.getString("store_id");
                                                ciudadEnvio = "-";
                                                provinciaS = "-";
                                                codigoPostalEnvio = "-";
                                                observacionesEnvio = "-";
                                                // Buscar almacen en atributos
                                                AlmacenDto almacenEntrega = null;
                                                for (AlmacenDto almacen : almacenes) {
                                                        if (storeIdML.equals(almacen.getAtributo(
                                                                        MercadoLibreConstants.STR_ATR_ALMACEN_ID_ML
                                                                                        + idSiteVenta))) {
                                                                almacenEntrega = almacen;
                                                                break;
                                                        }
                                                }
                                                if (almacenEntrega == null) {
                                                        procesoInfo.addDescripcion(
                                                                        "Can't find click&collect store for order");
                                                        throw new Exception("Can't find click&collect store for order "
                                                                        + orderId);
                                                }
                                                // Meter tag almacen en pedido
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_ID_TIENDA,
                                                                almacenEntrega.getIdTiendaExterno()));
                                                // Meter tipo pedido recogida en tienda
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_TIPO_ENVIO,
                                                                XMLOrderConstants.VALUE_TIENDA));

                                        } else {
                                                if (hasShippingInfo) {
                                                        rua = order.getJSONObject("shipping")
                                                                        .getJSONObject("receiver_address")
                                                                        .getString("street_name");
                                                        try {
                                                                numero = order.getJSONObject("shipping")
                                                                                .getJSONObject("receiver_address")
                                                                                .getString("number");
                                                        } catch (Exception e) {
                                                        }
                                                        observacionesEnvio = order.getJSONObject("shipping")
                                                                        .getJSONObject("receiver_address")
                                                                        .getString("comment");
                                                        bairro = order.getJSONObject("shipping")
                                                                        .getJSONObject("receiver_address")
                                                                        .getJSONObject("neighborhood")
                                                                        .getString("name");
                                                        ciudadEnvio = order.getJSONObject("shipping")
                                                                        .getJSONObject("receiver_address")
                                                                        .getJSONObject("city").getString("name");
                                                        provinciaS = order.getJSONObject("shipping")
                                                                        .getJSONObject("receiver_address")
                                                                        .getJSONObject("state").getString("name");
                                                        codigoPostalEnvio = order.getJSONObject("shipping")
                                                                        .getJSONObject("receiver_address")
                                                                        .getString("zip_code");
                                                        observacionesEnvio = order.getJSONObject("shipping")
                                                                        .getJSONObject("receiver_address")
                                                                        .getString("comment");
                                                        procesoInfo.addDescripcion("provincia(" + hasShippingInfo + ")="
                                                                        + provinciaS);
                                                        try {
                                                                tipoEnvio = order.getJSONObject("shipping")
                                                                                .getString("logistic_type");
                                                                procesoInfo.addDescripcion("tipoEnvio:" + tipoEnvio);
                                                        } catch (Exception e) {
                                                                net.sourceforge.jtds.util.Logger.logException(e);
                                                        }
                                                } else {
                                                        Long idShipping = null;
                                                        String shippingInfoStr = null;
                                                        try {
                                                                idShipping = order.getJSONObject("shipping")
                                                                                .getLong("id");
                                                                JSONObject shippingInfo = shippingMercadoLivreConnector
                                                                                .get(mercado, idShipping, procesoInfo);
                                                                rua = shippingInfo.getJSONObject("receiver_address")
                                                                                .getString("street_name");
                                                                numero = shippingInfo.getJSONObject("receiver_address")
                                                                                .getString("street_number");
                                                                observacionesEnvio = shippingInfo
                                                                                .getJSONObject("receiver_address")
                                                                                .getString("comment");
                                                                bairro = shippingInfo.getJSONObject("receiver_address")
                                                                                .getJSONObject("neighborhood")
                                                                                .getString("name");
                                                                ciudadEnvio = shippingInfo
                                                                                .getJSONObject("receiver_address")
                                                                                .getJSONObject("city")
                                                                                .getString("name");
                                                                provinciaS = shippingInfo
                                                                                .getJSONObject("receiver_address")
                                                                                .getJSONObject("state")
                                                                                .getString("name");
                                                                codigoPostalEnvio = shippingInfo
                                                                                .getJSONObject("receiver_address")
                                                                                .getString("zip_code");
                                                                procesoInfo.addDescripcion("provincia("
                                                                                + hasShippingInfo + ")=" + provinciaS);

                                                                try {
                                                                        tipoEnvio = shippingInfo
                                                                                        .getString("logistic_type");
                                                                        procesoInfo.addDescripcion(
                                                                                        "tipoEnvio:" + tipoEnvio);

                                                                } catch (Exception e) {
                                                                        // TODO: handle exception
                                                                }
                                                        } catch (Exception e) {
                                                                procesoInfo.addDescripcion("Error reading shippingInfo "
                                                                                + idShipping + " for order " + orderId
                                                                                + " (" + order.getJSONObject("shipping")
                                                                                + "): " + shippingInfoStr, e);
                                                        }
                                                }

                                                if (TypeTools.getBoolean(credentials,
                                                                Credenciales.MERCADO_IGNORE_FULFILLMENT_ORDERS)
                                                                && LogisticType.FULFILLMENT.is(tipoEnvio)) {
                                                        procesoInfo.addWarn(
                                                                        "The order ignored because it was fulfilled:\n orderId :"
                                                                                        + orderId);
                                                        continue;
                                                }

                                                if (LogisticType.SELF_SERVICE.is(tipoEnvio)) {
                                                        xmlPedidoHermes.append(XmlTools.openTag(nivel,
                                                                        PublicConstants.TAG_ATRIBUTOS));
                                                        xmlPedidoHermes.append(XmlTools.openTag(nivel,
                                                                        PublicConstants.TAG_ATRIBUTO));
                                                        try {
                                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                                PublicConstants.TAG_ATRIBUTO_NOMBRE,
                                                                                "entregaExpress"));
                                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                                PublicConstants.TAG_ATRIBUTO_VALOR,
                                                                                "si"));
                                                        } catch (Exception e) {

                                                        }
                                                        xmlPedidoHermes.append(XmlTools.closeTag(nivel,
                                                                        PublicConstants.TAG_ATRIBUTO));
                                                        xmlPedidoHermes.append(XmlTools.closeTag(nivel,
                                                                        PublicConstants.TAG_ATRIBUTOS));
                                                }
                                                if (SITE_ML != null && SITE_ML.equals("MCO")) {
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        XMLOrderConstants.TAG_VIA_DESTINATARIO,
                                                                        rua + " " + numero));
                                                } else {
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        XMLOrderConstants.TAG_VIA_DESTINATARIO, rua));
                                                }
                                                // xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                // XMLOrderConstants.TAG_VIA_DESTINATARIO, rua));
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_NUMERO_ENVIO, numero));
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_COMPLEMENTO_DESTINATARIO,
                                                                observacionesEnvio));
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_COLONIA_ENVIO, bairro));
                                                // xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                // XMLOrderConstants.TAG_DOMICILIO_ENVIO, domicilioEnvio));
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_POBLACION_ENVIO, ciudadEnvio));
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_LOCALIDAD_DESTINATARIO,
                                                                ciudadEnvio));
                                                String provinciaAlternativa = MercadoLibreConstants.mapaProvincias
                                                                .get(provinciaS.toUpperCase());

                                                if (!StringUtils.isEmpty(provinciaAlternativa)) {
                                                        provinciaS = provinciaAlternativa;
                                                }
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_PROVINCIA_ENVIO, provinciaS));
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_CODIGO_POSTAL_ENVIO,
                                                                codigoPostalEnvio));
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_OBSERVACIONES,
                                                                observacionesEnvio));
                                        }

                                        JSONObject phone = order.getJSONObject("buyer").getJSONObject("phone");
                                        String telefono = "-";
                                        try {
                                                telefono = phone.getString("number");
                                                if (StringUtils.isEmpty(telefono)) {
                                                        telefono = "-";
                                                }
                                        } catch (Exception e) {
                                                // ??????
                                        }

                                        String totalShipping = this.getTotalShippingCost(order, procesoInfo);
                                        if (org.apache.commons.lang3.StringUtils.isNotBlank(totalShipping)) {
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_GASTOS_ENVIO, totalShipping));
                                        }

                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                        XMLOrderConstants.TAG_TELEFONO_MOVIL_ENVIO, telefono));
                                        if (StringUtils.isEmpty(email)) {
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_EMAIL_ENVIO, "sinmail@ml.com"));
                                        } else {
                                                xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                XMLOrderConstants.TAG_EMAIL_ENVIO, email));
                                        }

                                        xmlPedidoHermes.append(
                                                        XmlTools.printTag(nivel, XMLOrderConstants.TAG_IMPORTE_TOTAL,
                                                                        getPaidAmount(order).replace(',', '.')));

                                        xmlPedidoHermes.append(
                                                        XmlTools.openTag(nivel, XMLOrderConstants.TAG_PRODUCTOS));
                                        nivel++;

                                        JSONArray orderItems = (JSONArray) order.getJSONArray("order_items");

                                        for (int j = 0; j < orderItems.size(); j++) {
                                                JSONObject orderItem = orderItems.getJSONObject(j);

                                                Long cantidadComprada = orderItem.getLong("quantity");
                                                Double comisionAcumulada = 0.0;
                                                Double comisionTotal = 0.0;
                                                try {
                                                        if (orderItem.containsKey("sale_fee")) {
                                                                comisionTotal = orderItem.getDouble("sale_fee");
                                                        }
                                                } catch (Exception e) {
                                                        try {
                                                                comisionTotal = orderItem.getLong("sale_fee") * 1.0;
                                                        } catch (Exception ex) {
                                                                procesoInfo.addDescripcion(
                                                                                "Error leyendo comision. Item="
                                                                                                + orderItem.toString(4),
                                                                                ex);
                                                        }
                                                }
                                                for (int y = 0; y < cantidadComprada; y++) {
                                                        procesoInfo.addDescripcion("--------Items para la transaccion "
                                                                        + i + ": " + orderItems.size() + "\n");
                                                        String seller_custom_field = orderItem.getJSONObject("item")
                                                                        .getString("seller_custom_field");

                                                        String variation_id = orderItem.getJSONObject("item")
                                                                        .getString("variation_id");
                                                        String itemId = orderItem.getJSONObject("item")
                                                                        .getString("id");
                                                        procesoInfo.addDescripcion("seller_custom_field:"
                                                                        + seller_custom_field + "\n");
                                                        procesoInfo.addDescripcion(
                                                                        "variation_id:" + variation_id + "\n");
                                                        procesoInfo.addDescripcion("itemId:" + itemId + "\n");
                                                        procesoInfo.addDescripcion(
                                                                        "site.getIdSite():" + site.getIdSite() + "\n");
                                                        xmlPedidoHermes.append(XmlTools.openTag(nivel,
                                                                        XMLOrderConstants.TAG_PRODUCTO));
                                                        nivel++;
                                                        SubProductoDto subProducto = null;
                                                        // buscamos por ids de ML
                                                        List<CanalSiteProductoM> canalsSite = canalSiteProductoRepositoryM
                                                                        .findAllByIdSiteCanalAndIdItemChannel(
                                                                                        cmCanalSite.getIdCanalSite(),
                                                                                        itemId);
                                                        ProductoCollectionDto pr = null;
                                                        if (canalsSite != null && canalsSite.size() > 0) {
                                                                procesoInfo.addDescripcion(
                                                                                "encontramos id de ML, buscmoas producto="
                                                                                                + canalsSite.get(0)
                                                                                                                .getIdProducto()
                                                                                                + " en site="
                                                                                                + site.getIdSite());
                                                                pr = hermesCore.leerProductoPorIdProducto(
                                                                                canalsSite.get(0).getIdProducto());

                                                                if (pr != null && pr.getSubProductos() != null) {
                                                                        procesoInfo.addDescripcion(
                                                                                        "encontramos producto=" + pr
                                                                                                        .getCodigoAlfa());
                                                                        for (SubProductoDto sub : pr
                                                                                        .getSubProductos()) {
                                                                                if (!StringUtils.isEmpty(sub
                                                                                                .getAtributoStr("ID_ITEM_ML"))
                                                                                                && sub.getAtributoStr(
                                                                                                                "ID_ITEM_ML")
                                                                                                                .equals(variation_id)) {
                                                                                        subProducto = sub;
                                                                                        break;
                                                                                }

                                                                                boolean isBrazilEnvironment = this.productTools
                                                                                                .getBrazilianEnvironment(
                                                                                                                credentials);
                                                                                if (isBrazilEnvironment) {
                                                                                        List<CategoriaCanalAtributoBD> attributes = getAttributes(
                                                                                                        mercado,
                                                                                                        cmCanalSite.getIdSiteRotulo(),
                                                                                                        pr.getIdCategorias(),
                                                                                                        procesoInfo);

                                                                                        if (!attributes.stream()
                                                                                                        .anyMatch(attribute -> attribute
                                                                                                                        .is("SIZE"))) {
                                                                                                subProducto = sub;
                                                                                                break;
                                                                                        }
                                                                                }
                                                                        }
                                                                }
                                                        }

                                                        if (subProducto == null) {
                                                                if (seller_custom_field == null)
                                                                        seller_custom_field = "NOT_PRESENT";
                                                                procesoInfo.addDescripcion(
                                                                                "no encontramos producto por busqueda de ML, buscamos por seller_custom="
                                                                                                + seller_custom_field
                                                                                                + "\n");

                                                                subProducto = hermesCore
                                                                                .leerSubProductoPorIdSiteIdArticuloModalia(
                                                                                                idSiteProducto,
                                                                                                seller_custom_field);
                                                                if (subProducto == null) {

                                                                        String codigoAlfa = seller_custom_field;
                                                                        String talla = "";
                                                                        try {
                                                                                JSONArray variations = (JSONArray) orderItem
                                                                                                .getJSONObject("item")
                                                                                                .getJSONArray("variation_attributes");
                                                                                for (int k = 0; k < variations
                                                                                                .size(); k++) {
                                                                                        JSONObject variation = variations
                                                                                                        .getJSONObject(k);

                                                                                        String id = (String) variation
                                                                                                        .get("id");
                                                                                        procesoInfo.addDescripcion(
                                                                                                        "id=" + id);
                                                                                        if (id.equals("SIZE")) {
                                                                                                talla = id = (String) variation
                                                                                                                .get("value_name");
                                                                                                ;
                                                                                        }
                                                                                }

                                                                        } catch (Exception e) {
                                                                                codigoAlfa = seller_custom_field;
                                                                                JSONArray variations = (JSONArray) orderItem
                                                                                                .getJSONObject("item")
                                                                                                .getJSONArray("variation_attributes");
                                                                                for (int k = 0; k < variations
                                                                                                .size(); k++) {
                                                                                        JSONObject variation = variations
                                                                                                        .getJSONObject(k);
                                                                                        String id = (String) variation
                                                                                                        .get("id");
                                                                                        procesoInfo.addDescripcion(
                                                                                                        "id=" + id);
                                                                                        if (id.equals("SIZE")) {
                                                                                                talla = id = (String) variation
                                                                                                                .get("value_name");
                                                                                                ;
                                                                                        }
                                                                                }
                                                                                procesoInfo.addDescripcion(
                                                                                                "codigoAlfa error="
                                                                                                                + codigoAlfa);
                                                                                procesoInfo.addDescripcion(
                                                                                                "talla erro=" + talla);
                                                                        }

                                                                        String alfaSub = "";
                                                                        try {
                                                                                alfaSub = seller_custom_field.substring(
                                                                                                0, seller_custom_field
                                                                                                                .lastIndexOf("-"));
                                                                        } catch (Exception e) {

                                                                        }
                                                                        procesoInfo.addDescripcion(
                                                                                        "alfaSub=" + alfaSub);
                                                                        procesoInfo.addDescripcion(
                                                                                        "codigoAlfa=" + codigoAlfa);
                                                                        procesoInfo.addDescripcion("talla=" + talla);
                                                                        if (pr == null || pr.getIdProducto() == null) {
                                                                                pr = hermesCore.getDetallesProductoByIdArticuloModalia(
                                                                                                site.getIdSite(),
                                                                                                seller_custom_field);
                                                                                if (pr == null || pr
                                                                                                .getIdProducto() == null) {
                                                                                        List<ProductoCollectionDto> list = hermesCore
                                                                                                        .leerProductoByCodigoAlfaAndIdSite(
                                                                                                                        codigoAlfa,
                                                                                                                        idSiteProducto);
                                                                                        if (list != null && list
                                                                                                        .size() > 0) {
                                                                                                pr = list.get(0);
                                                                                        }
                                                                                }
                                                                                if (pr == null || pr
                                                                                                .getIdProducto() == null) {
                                                                                        List<ProductoCollectionDto> list = hermesCore
                                                                                                        .leerProductoByCodigoAlfaAndIdSite(
                                                                                                                        alfaSub,
                                                                                                                        idSiteProducto);
                                                                                        if (list != null && list
                                                                                                        .size() > 0) {
                                                                                                pr = list.get(0);
                                                                                        }
                                                                                        if (pr == null || pr
                                                                                                        .getIdProducto() == null) {
                                                                                                procesoInfo.addDescripcion(
                                                                                                                "Can't find product "
                                                                                                                                + seller_custom_field);
                                                                                                throw new Exception(
                                                                                                                "Can't find product "
                                                                                                                                + seller_custom_field);
                                                                                        }
                                                                                }
                                                                        }
                                                                        procesoInfo.addDescripcion("Producto alfa "
                                                                                        + codigoAlfa + " = "
                                                                                        + pr.getIdProducto() + " talla "
                                                                                        + URLEncoder.encode(talla,
                                                                                                        "UTF-8")
                                                                                                        .replace("+", "%20"));

                                                                        if (!StringUtils.isEmpty(variation_id)) {
                                                                                if (pr.getSubProductos() != null) {
                                                                                        for (SubProductoDto sub : pr
                                                                                                        .getSubProductos()) {
                                                                                                if (!StringUtils.isEmpty(
                                                                                                                sub.getAtributoStr(
                                                                                                                                "ID_ITEM_ML"))
                                                                                                                && sub.getAtributoStr(
                                                                                                                                "ID_ITEM_ML")
                                                                                                                                .equals(variation_id)) {
                                                                                                        subProducto = sub;
                                                                                                }
                                                                                        }
                                                                                }
                                                                        }
                                                                        if (subProducto == null) {
                                                                                subProducto = hermesCore
                                                                                                .leerSubProductoByIdProductoTallaOriginal(
                                                                                                                pr.getIdProducto(),
                                                                                                                talla);
                                                                                procesoInfo.addDescripcion(
                                                                                                "Producto alfa " + codigoAlfa
                                                                                                                + " = "
                                                                                                                + pr.getIdProducto()
                                                                                                                + " tallaEU "
                                                                                                                + talla);
                                                                                if (subProducto == null) {
                                                                                        subProducto = hermesCore
                                                                                                        .leerSubProductoByIdProductoTallaEu(
                                                                                                                        pr.getIdProducto(),
                                                                                                                        talla);
                                                                                }

                                                                                // SubProducto subProducto =
                                                                                // productosBL.leerSubProductoPorIdArticulo(seller_custom_field);
                                                                                if (subProducto == null) {
                                                                                        if (StringUtils.isEmpty(
                                                                                                        talla)) {
                                                                                                subProducto = pr.getSubProductos()
                                                                                                                .get(pr.getSubProductos()
                                                                                                                                .size()
                                                                                                                                - 1);
                                                                                        }
                                                                                        if (subProducto == null) {

                                                                                                // ultimo intento
                                                                                                String[] arrTokens = seller_custom_field
                                                                                                                .split("-");
                                                                                                try {
                                                                                                        codigoAlfa = seller_custom_field
                                                                                                                        .substring(0, seller_custom_field
                                                                                                                                        .lastIndexOf("-"));
                                                                                                        talla = arrTokens[arrTokens.length
                                                                                                                        - 1];
                                                                                                        subProducto = hermesCore
                                                                                                                        .leerSubProductoByIdProductoTallaOriginal(
                                                                                                                                        pr.getIdProducto(),
                                                                                                                                        talla);
                                                                                                        if (subProducto == null) {
                                                                                                                subProducto = hermesCore
                                                                                                                                .leerSubProductoByIdProductoTallaEu(
                                                                                                                                                pr.getIdProducto(),
                                                                                                                                                talla);
                                                                                                        }
                                                                                                } catch (Exception e) {

                                                                                                }
                                                                                                if (subProducto == null) {
                                                                                                        procesoInfo.addDescripcion(
                                                                                                                        "Can't find product "
                                                                                                                                        + seller_custom_field
                                                                                                                                        + " and size="
                                                                                                                                        + talla);
                                                                                                        throw new Exception(
                                                                                                                        "Can't find product "
                                                                                                                                        + seller_custom_field
                                                                                                                                        + " and size="
                                                                                                                                        + talla);
                                                                                                }
                                                                                        }
                                                                                }
                                                                        }

                                                                }
                                                        }
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        XMLOrderConstants.TAG_ID_PRODUCTO,
                                                                        "" + subProducto.getIdProducto()));
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        XMLOrderConstants.TAG_ID_SUBPRODUCTO,
                                                                        subProducto.getIdSubProducto() + ""));
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        XMLOrderConstants.TAG_IMPORTE,
                                                                        fNumero.format(orderItem
                                                                                        .getDouble("unit_price"))
                                                                                        .replace(',', '.')));
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        XMLOrderConstants.TAG_CANTIDAD, "1"));
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        XMLOrderConstants.TAG_REFERENCIA,
                                                                        subProducto.getIdArticuloModalia()));
                                                        xmlPedidoHermes.append(
                                                                        XmlTools.printTag(nivel,
                                                                                        XMLOrderConstants.TAG_NUMERO_PEDIDO_LINEA_ORIGINAL,
                                                                                        order.getString("id") + ""));
                                                        String idOrderOriginal = orderItem.getString(
                                                                        MercadoLibreConstants.ATR_ORIGINAL_ORDER_ID);
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        "ATRIBUTO_NOMBRE_1", "idOrderOriginal"));
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        "ATRIBUTO_VALOR_1", idOrderOriginal));

                                                        Double comisionArticulo = TypeTools
                                                                        .divideDobleEnIgualesConRedondeo(comisionTotal,
                                                                                        comisionAcumulada, y,
                                                                                        orderItems.size());
                                                        comisionAcumulada += comisionArticulo;
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        "ATRIBUTO_NOMBRE_2", "comisionArticulo"));
                                                        xmlPedidoHermes.append(XmlTools.printTag(nivel,
                                                                        "ATRIBUTO_VALOR_2",
                                                                        fNumero.format(comisionArticulo)));
                                                        nivel--;
                                                        xmlPedidoHermes.append(XmlTools.closeTag(nivel,
                                                                        XMLOrderConstants.TAG_PRODUCTO));
                                                }
                                        }
                                        nivel--;

                                        xmlPedidoHermes.append(
                                                        XmlTools.closeTag(nivel, XMLOrderConstants.TAG_PRODUCTOS));
                                        nivel--;
                                        xmlPedidoHermes.append(
                                                        XmlTools.closeTag(nivel, XMLOrderConstants.TAG_SOLICITUD));
                                        procesoInfo.addDescripcion(
                                                        "\n------------------------------------------------------ FIN DE TRANSACCION\n\n");
                                        procesoInfo.addDescripcion("xmlPedidoHermes=" + xmlPedidoHermes);
                                        if (xmlPedidoHermes != null
                                                        && !StringUtils.isEmpty(xmlPedidoHermes.toString())) {
                                                nivel--;
                                                xmlPedidoHermes.append(XmlTools.closeTag(nivel,
                                                                XMLOrderConstants.TAG_SOLICITUDES));
                                                procesoInfo.addDescripcion("ORDER " + orderId + " HERMES XML = "
                                                                + xmlPedidoHermes.toString());
                                                resultadoPedido = hermesService.insertOrder(site.getIdSite(),
                                                                xmlPedidoHermes.toString(), procesoInfo);

                                                if (resultadoPedido.getError() != null && resultadoPedido.getError()
                                                                .contains("esta duplicado para el site")) {
                                                        procesoInfo.addDescripcion(
                                                                        "no anyadimos por error de duplicado");
                                                        continue;
                                                }

                                        }
                                        procesoInfo.addDescripcion(
                                                        "RESULT=" + BeanDescriber.stringify(resultadoPedido));
                                        resultadoPedido.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                        publicationResultService.guardarResultadoPedido(resultadoPedido);
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                        procesoInfo.addDescripcion("", e);
                                        resultadoPedido.setOk(false);
                                        resultadoPedido.setError(
                                                        e.toString() + "|" + orders.getJSONObject(i).toString(4));
                                        publicationResultService.guardarResultadoPedido(resultadoPedido);
                                        monitor.alert(cmCanalSite.getIdCanal().getIdCanal(),
                                                        cmCanalSite.getIdSiteRotulo(), "ImportOrders");
                                }

                        }
                }

                procesoInfo.addDescripcion("getOrders END");
                monitor.event(cmCanalSite.getIdCanal().getIdCanal(), cmCanalSite.getIdSiteRotulo(), "ImportOrders");
                return;
        }

        private String toString(JSONObject value) {
                if (value != null) {
                        return value.toString();
                }
                return null;
        }

        @Override
        /**
         * Elimina publicaciones en mercadolibre
         */
        public void eliminarPublicaciones(SiteDto site, List<CredentialsSiteChannelDto> credentials,
                        List<ProductoCollectionDto> productos, ProcesoInfo procesoInfo, CanalSiteBD cmCanalSite)
                        throws Exception {
                ResultadoPublicacionM resultadoPublicacion = (ResultadoPublicacionM) procesoInfo
                                .getResultadoPublicacion();

                procesoInfo.addDescripcion("-----------------------eliminarPublicaciones idSite " + site.getIdSite());
                procesoInfo.addDescripcion("-----------------------productos=" + productos.size()
                                + " -----------------------------------");

                if (productos.size() > 0) {
                        for (ProductoCollectionDto producto : productos) {

                                ResultadoPublicacionProductoM resultDetalle = new ResultadoPublicacionProductoM();
                                // Si ya está publicado, entonces no hay que enviar de nuevo este producto.
                                // Meter el producto en la colección CANAL_SITE_PRODUCTO con los datos
                                // actualizados
                                CanalSiteProductoM canalSiteProducto = null;

                                List<CanalSiteProductoM> canalSiteProductoList = canalSiteProductoRepositoryM
                                                .findAllByIdSiteCanalAndIdProducto(cmCanalSite.getIdCanalSite(),
                                                                producto.getIdProducto());
                                if (canalSiteProductoList != null && !canalSiteProductoList.isEmpty()) {
                                        canalSiteProducto = canalSiteProductoList.get(0);
                                } else {
                                        procesoInfo.addDescripcion("el producto=" + producto.getCodigoAlfa()
                                                        + " no esta publicado todavia, continuamos");
                                        continue;
                                }

                                if (!canalSiteProducto.getPublicado()) {
                                        procesoInfo.addDescripcion("el producto=" + producto.getCodigoAlfa()
                                                        + " no esta publicado todavia, continuamos");
                                        continue;
                                }

                                resultadoPublicacion.setCountProducts(resultadoPublicacion.getCountProducts() + 1);

                                procesoInfo.addDescripcion("" + BeanDescriber.stringify(canalSiteProducto));

                                resultDetalle.setIdProducto(producto.getIdProducto());
                                resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());

                                List<ResultadoPublicacionProductoVariantM> resultsSub = new ArrayList<ResultadoPublicacionProductoVariantM>();
                                Integer stock = 0;
                                Meli mercado = null;
                                try {
                                        mercado = getMeli(credentials, false, site.getIdSite(), procesoInfo);

                                        if (!producto.getActivo()) {
                                                cambiarSubProductosStockCero(producto);
                                                resultDetalle.setError("El producto esta como inactivo en el site");
                                        }

                                        String itemID = canalSiteProducto.getIdItemChannel();

                                        // Dejamos stock a 0 (eliminar de la publicación)
                                        cambiarSubProductosStockCero(producto);
                                        String garantia = TypeTools.getCredential(credentials,
                                                        Credenciales.MERCADO_GARANTIA);
                                        // resultDetalle = updateVariantesInternal(producto,
                                        // site.getIdSite().toString(), site, true, itemID, mercado, garantia,
                                        // procesoInfo,resultDetalle,false, credentials);

                                        deleteProduct(producto, site, itemID, mercado, resultDetalle, credentials,
                                                        procesoInfo);

                                        // Meter el producto en la colección CANAL_SITE_PRODUCTO con los datos
                                        // actualizados
                                        if (resultDetalle.getIdItemChannel() != null && resultDetalle.isOk()) {
                                                CanalSiteProductoM canalSiteProductoUpdate = new CanalSiteProductoM(
                                                                canalSiteProducto != null ? canalSiteProducto.get_id()
                                                                                : null,
                                                                cmCanalSite.getIdCanalSite(), producto.getIdProducto(),
                                                                itemID, false,
                                                                producto.getPrecio() + "",
                                                                producto.getPrecioRebajado() + "", stock + "", "",
                                                                procesoInfo.getResultadoPublicacion()
                                                                                .getIdPublicacion());

                                                resultDetalle.setPrice(producto.getPrecio() + "");
                                                resultDetalle.setPriceDiscount(producto.getPrecioRebajado() + "");
                                                resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
                                                resultDetalle.setIdItemChannel(itemID);
                                                resultDetalle.setDeleted(true);
                                                // Dejamos de publicarlo si se ha realizado la operación de forma
                                                // correcta.
                                                canalSiteProductoUpdate.setPublicado(false);
                                                publicationResultService.guardarResultadoProducto(resultDetalle,
                                                                canalSiteProductoUpdate,
                                                                procesoInfo);

                                                procesoInfo.setResultadosOk(procesoInfo.getResultadosOk() + 1);
                                        } else {
                                                if (resultDetalle.getError() != null) {
                                                        canalSiteProducto.setError(resultDetalle.getError());
                                                }
                                                publicationResultService.guardarResultadoProducto(resultDetalle,
                                                                canalSiteProducto,
                                                                procesoInfo);
                                        }

                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("Error Mercadolibre product "
                                                        + producto.getIdProducto() + " : " + e.getMessage(), e);
                                        resultDetalle.setOk(false);
                                        resultDetalle.setError(e.getMessage());
                                        resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
                                        publicationResultService.guardarResultadoProducto(resultDetalle, null,
                                                        procesoInfo);
                                        monitor.alert(cmCanalSite.getIdCanal().getIdCanal(),
                                                        cmCanalSite.getIdSiteRotulo(), "EliminatePublications");
                                }
                                resultadoPublicacion.getProducts().add(resultDetalle);
                        }
                }
                procesoInfo.addDescripcion("***********Mercadolibre*************  fin proceso - Eliminar productos: "
                                + productos.size());
                monitor.event(cmCanalSite.getIdCanal().getIdCanal(), cmCanalSite.getIdSiteRotulo(),
                                "EliminatePublications");
        }

        /**
         * Build sample variations
         * 
         * @return VariationsType object
         */
        public Integer getStock(ProductoCollectionDto producto) throws Exception {

                Integer stock = 0;
                log.info("getSubProductos=" + producto.getSubProductos().size());
                for (SubProductoDto subProducto : producto.getSubProductos()) {
                        stock = stock + subProducto.getStockEnCanal().intValue();
                }
                return stock;
        }

        private CredentialsSiteChannelDto getCode(List<CredentialsSiteChannelDto> credentials) throws Exception {
                CredentialsSiteChannelDto accessToken = null;
                for (CredentialsSiteChannelDto credential : credentials) {
                        if (credential.getKey().equals(Credenciales.MERCADO_AUTH_TOKEN)) {
                                accessToken = credential;
                        }
                }
                if (accessToken == null || StringUtils.isEmpty(accessToken.getValue())) {
                        throw new IntegrationExecuteException("MERCADO_AUTH_TOKEN not defined");
                }
                return accessToken;
        }

        public void deleteProduct(ProductoCollectionDto producto, SiteDto site, String itemId, Meli mercado,
                        ResultadoPublicacionProductoM resultDetail, List<CredentialsSiteChannelDto> credentials,
                        @SuppressWarnings("rawtypes") ProcesoInfo procesoInfo) throws Exception {
                String result = "";
                resultDetail.setOk(true);
                try {
                        procesoInfo.addDescripcion("item=" + itemId);
                        FluentStringsMap params = new FluentStringsMap();
                        org.json.JSONObject productoJson = new org.json.JSONObject();
                        productoJson.put("status", "closed");
                        params.add("access_token", mercado.getAccessToken());
                        Response r = null;
                        procesoInfo.addDescripcion(productoJson.toString());
                        String id = "";
                        r = mercado.put("/items/" + itemId, params, productoJson.toString());
                        procesoInfo.addDescripcion("closed=" + r.getResponseBody());

                        productoJson = new org.json.JSONObject();
                        productoJson.put("deleted", "true");
                        params.add("access_token", mercado.getAccessToken());
                        r = null;
                        procesoInfo.addDescripcion(productoJson.toString());
                        id = "";
                        r = mercado.put("/items/" + itemId, params, productoJson.toString());
                        procesoInfo.addDescripcion("deleted=" + r.getResponseBody());

                        try {
                                String error = "";
                                org.json.JSONObject jObject = new org.json.JSONObject(r.getResponseBody()); // json
                                String seller_id = null;
                                String status = null;
                                String message = null;
                                String errorMsg = null;

                                try {
                                        seller_id = jObject.getString("id");
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                }
                                try {
                                        status = jObject.getString("status");
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                }
                                try {
                                        message = jObject.getString("message");
                                } catch (Exception e) {
                                }
                                try {
                                        errorMsg = jObject.getString("error");
                                } catch (Exception e) {
                                }

                                procesoInfo.addDescripcion("seller_id=" + seller_id);
                                procesoInfo.addDescripcion("status=" + status);
                                procesoInfo.addDescripcion("message=" + message);
                                procesoInfo.addDescripcion("error=" + errorMsg);

                                if (StringUtils.isEmpty(seller_id)) {
                                        resultDetail.setOk(false);
                                        resultDetail.setError(r.getResponseBody());
                                        return;
                                }

                                for (SubProductoDto sb : producto.getSubProductos()) {
                                        sb.setAtributo("ID_ITEM_ML", (String) null);
                                        hermesCore.actualizarSubProductoAtributos(sb.getIdSubProducto(),
                                                        JSONObject.fromObject(sb.getAtributos()));
                                }

                                resultDetail.setIdItemChannel(seller_id);
                                // resultDetail.setSkuHermesChannel(producto.getIdArticuloModalia());
                                resultDetail.setSkuHermesChannel(producto.getIdArticuloModalia());

                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                                resultDetail.setOk(false);
                                resultDetail.setError(e.getMessage());
                        }
                } catch (Exception e) {
                        procesoInfo.addDescripcion("", e);
                        resultDetail.setError(e.getMessage());
                        resultDetail.setOk(false);
                        resultDetail.setSkuHermesChannel(producto.getIdArticuloModalia());
                }
        }

        public Meli setOAuthReceiver(String code, List<CredentialsSiteChannelDto> credentials, Long idSite,
                        ProcesoInfo procesoInfo2) throws Exception {
                try {
                        Long clientId = Long.parseLong(
                                        TypeTools.getCredential(credentials, Credenciales.MERCADO_CLIENT_ID));
                        String clientSecret = TypeTools.getCredential(credentials, Credenciales.MERCADO_CLIENT_SECRET);

                        Meli mercado = new Meli(clientId, clientSecret);
                        mercado.authorize(code, TypeTools.getCredential(credentials, Credenciales.MERCADO_AUTH_URL),
                                        procesoInfo2); // Esto nos crea el access
                        mercado.code = code;
                        CredentialsSiteChannelDto refreshToken = TypeTools.getCredentialBean(credentials,
                                        Credenciales.MERCADO_REFRESH_TOKEN);
                        if (procesoInfo2 != null)
                                procesoInfo2.addDescripcion(BeanDescriber.stringify(mercado));
                        if (!StringUtils.isEmpty(mercado.refreshToken)) {
                                refreshToken.setValue(mercado.refreshToken);
                                TypeTools.updateCredentialBean(credentials, Credenciales.MERCADO_REFRESH_TOKEN,
                                                mercado.refreshToken);
                                if (procesoInfo2 != null)
                                        procesoInfo2.addDescripcion(BeanDescriber.stringify(refreshToken));
                                credentialsService.saveCredential(refreshToken);
                        }
                        CredentialsSiteChannelDto accessToken = TypeTools.getCredentialBean(credentials,
                                        Credenciales.MERCADO_AUTH_TOKEN);
                        if (!StringUtils.isEmpty(mercado.getAccessToken())) {
                                accessToken.setValue(mercado.getAccessToken());
                                TypeTools.updateCredentialBean(credentials, Credenciales.MERCADO_AUTH_TOKEN,
                                                mercado.getAccessToken());
                                if (procesoInfo2 != null)
                                        procesoInfo2.addDescripcion(BeanDescriber.stringify(accessToken));
                                credentialsService.saveCredential(accessToken);
                        }
                        credentials = credentialsService.getCredentialsBySiteChannel(idSite);
                        hashMelis.put(idSite, mercado);
                        return mercado;
                } catch (Exception e) {
                        if (procesoInfo2 != null)
                                procesoInfo2.addDescripcion("", e);
                        log.error("", e);
                }
                return null;
        }

        public Meli getAccessTokenNew(Meli mercado, List<CredentialsSiteChannelDto> credentials, Long idSite,
                        ProcesoInfo procesoInfo) throws Exception {
                if (mercado == null) {
                        Long clientId = Long.parseLong(
                                        TypeTools.getCredential(credentials, Credenciales.MERCADO_CLIENT_ID));
                        String clientSecret = TypeTools.getCredential(credentials, Credenciales.MERCADO_CLIENT_SECRET);
                        mercado = new Meli(clientId, clientSecret);
                        mercado.refreshToken = TypeTools.getCredential(credentials, Credenciales.MERCADO_REFRESH_TOKEN);
                        // mercado.code = TypeTools.getCredential(credentials,
                        // Credenciales.MERCADO_AUTH_TOKEN);
                        mercado.refreshAccessToken(procesoInfo);
                        procesoInfo.addDescripcion("code=" + mercado.code);
                        procesoInfo.addDescripcion("refreshToken=" + mercado.refreshToken);
                        procesoInfo.addDescripcion("access token=" + mercado.getAccessToken());
                        CredentialsSiteChannelDto refreshToken = TypeTools.getCredentialBean(credentials,
                                        Credenciales.MERCADO_REFRESH_TOKEN);
                        if (!StringUtils.isEmpty(mercado.refreshToken)) {
                                refreshToken.setValue(mercado.refreshToken);
                                TypeTools.updateCredentialBean(credentials, Credenciales.MERCADO_REFRESH_TOKEN,
                                                mercado.refreshToken);
                                credentialsService.saveCredential(refreshToken);
                        } else {
                                procesoInfo.addDescripcion("Refresh token is null");
                        }
                        CredentialsSiteChannelDto accessToken = TypeTools.getCredentialBean(credentials,
                                        Credenciales.MERCADO_AUTH_TOKEN);
                        if (!StringUtils.isEmpty(mercado.getAccessToken())) {
                                accessToken.setValue(mercado.getAccessToken());
                                TypeTools.updateCredentialBean(credentials, Credenciales.MERCADO_AUTH_TOKEN,
                                                mercado.getAccessToken());
                                credentialsService.saveCredential(accessToken);
                        } else {
                                procesoInfo.addDescripcion("Access token is null");

                        }
                        credentials = credentialsService.getCredentialsBySiteChannel(idSite);
                        hashMelis.put(idSite, mercado);
                }
                return mercado;

        }

        public Meli getAccessToken(Meli mercado, List<CredentialsSiteChannelDto> credentials, Long idSite,
                        ProcesoInfo procesoInfo) throws Exception {
                if (mercado == null) {
                        Long clientId = Long.parseLong(
                                        TypeTools.getCredential(credentials, Credenciales.MERCADO_CLIENT_ID));
                        String clientSecret = TypeTools.getCredential(credentials, Credenciales.MERCADO_CLIENT_SECRET);
                        mercado = new Meli(clientId, clientSecret);
                        mercado.refreshToken = TypeTools.getCredential(credentials, Credenciales.MERCADO_REFRESH_TOKEN);
                        // mercado.code = TypeTools.getCredential(credentials,
                        // Credenciales.MERCADO_AUTH_TOKEN);
                        mercado.refreshAccessToken(procesoInfo);
                        procesoInfo.addDescripcion("code=" + mercado.code);
                        procesoInfo.addDescripcion("refreshToken=" + mercado.refreshToken);
                        procesoInfo.addDescripcion("access token=" + mercado.getAccessToken());
                        CredentialsSiteChannelDto refreshToken = TypeTools.getCredentialBean(credentials,
                                        Credenciales.MERCADO_REFRESH_TOKEN);
                        if (!StringUtils.isEmpty(mercado.refreshToken)) {
                                refreshToken.setValue(mercado.refreshToken);
                                TypeTools.updateCredentialBean(credentials, Credenciales.MERCADO_REFRESH_TOKEN,
                                                mercado.refreshToken);
                                credentialsService.saveCredential(refreshToken);
                        } else {
                                procesoInfo.addDescripcion("Refresh token is null");
                        }
                        CredentialsSiteChannelDto accessToken = TypeTools.getCredentialBean(credentials,
                                        Credenciales.MERCADO_AUTH_TOKEN);
                        if (!StringUtils.isEmpty(mercado.getAccessToken())) {
                                accessToken.setValue(mercado.getAccessToken());
                                TypeTools.updateCredentialBean(credentials, Credenciales.MERCADO_AUTH_TOKEN,
                                                mercado.getAccessToken());
                                credentialsService.saveCredential(accessToken);
                        } else {
                                procesoInfo.addDescripcion("Access token is null");

                        }
                        credentials = credentialsService.getCredentialsBySiteChannel(idSite);
                        hashMelis.put(idSite, mercado);
                }
                return mercado;

        }

        public Meli getMeli(List<CredentialsSiteChannelDto> credentials, boolean force, Long idSite,
                        ProcesoInfo procesoInfo) throws Exception {
                if (hashMelis.get(idSite) != null) {
                        // procesoInfo.addDescripcion("recuperamos de hash");
                        return hashMelis.get(idSite);
                }
                return getAccessToken(null, credentials, idSite, procesoInfo);
        }

        public Meli getMeliInicial(List<CredentialsSiteChannelDto> credentials, Long idSite) throws Exception {
                Meli mercado = null;
                Long clientId = Long.parseLong(TypeTools.getCredential(credentials, Credenciales.MERCADO_CLIENT_ID));
                String clientSecret = TypeTools.getCredential(credentials, Credenciales.MERCADO_CLIENT_SECRET);
                mercado = new Meli(clientId, clientSecret);
                return mercado;
        }

        private void cambiarSubProductosStockCero(ProductoCollectionDto producto) {

                for (SubProductoDto subProducto : producto.getSubProductos()) {
                        subProducto.setStockEnCanal(0L);
                }
        }

        /*
         * public String getMercadolibreNotification(String idSite,
         * List<CredentialsSiteChannelDto> credentials, @SuppressWarnings("rawtypes")
         * ProcesoInfo procesoInfo)
         * throws Exception {
         * procesoInfo.addDescripcion("Notification order START");
         * String result = "";
         * Meli mercado = getMeli(credentials, false,Long.parseLong(idSite),
         * procesoInfo);
         * FluentStringsMap params = new FluentStringsMap();
         * params.add("access_token", mercado.getAccessToken());
         * params.add("seller", TypeTools.getCredential(credentials,
         * Credenciales.MERCADO_LIBRE_ORDERID));
         * Response r = mercado.get("/orders/search/pending", params);
         * 
         * result += r.toString();
         * procesoInfo.addDescripcion("Notification order END");
         * return result;
         * }
         */

        private String getOrderNumber(PedidoDto pedido) {
                return "order Hermes = " + pedido.getNumero() + " / External = " + pedido.getIdPedidoExterno();
        }

        private String getProductNumber(ProductoCollectionDto producto) {
                return "product ID = " + producto.getIdProducto() + " / code = " + producto.getCodigoAlfa();
        }

        // TODO esto habra que engancharlo en algo programado
        public void updateStatusOrder(SiteDto site, List<CredentialsSiteChannelDto> credentials,
                        @SuppressWarnings("rawtypes") ProcesoInfo procesoInfo, CanalSiteBD cmCanalSite)
                        throws Exception {
                ResultadoPublicacionM resultadoPublicacion = (ResultadoPublicacionM) procesoInfo
                                .getResultadoPublicacion();
                resultadoPublicacion.setCountError(0L);
                resultadoPublicacion.setCountOrders(0L);
                procesoInfo.setResultadoPublicacion(resultadoPublicacion);
                Long idSite = site.getIdSite();
                procesoInfo.addDescripcion("updateStatusOrder INIT");
                Long idSiteVenta = credentials.get(0).getIdSiteCanal();
                Meli mercado = getMeli(credentials, false, idSite, procesoInfo);
                // leermos los pedidos pendientes para tracking
                String estados = env.getProperty("mercado.libre.estados.update." + idSite);
                List<Long> idEstados = null;
                if (StringUtils.isEmpty(estados)) {
                        estados = idEstadosPedidoUpdate;
                }
                idEstados = (List<Long>) Arrays.asList(estados.split(","));
                procesoInfo.addDescripcion("Requesting orders to update");
                List<PedidoDto> pedidos = hermesCore.findByIdSiteOriginalAndEstados(idSiteVenta, idEstados);
                procesoInfo.addDescripcion("Orders to update found: " + pedidos.size());
                procesoInfo.addDescripcion("idErp: " + site.getIdErp());
                List<String> originalOrderIdsTratados = new ArrayList<>();

                resultadoPublicacion.setCountOrders(new Long(pedidos.size()));
                for (PedidoDto pedidoAlmacen : pedidos) {
                        // if (!pedidoAlmacen.getNumero().equals("N4637372-C-1R-1A")) continue;
                        procesoInfo.addDescripcion("Checking status for order=" + pedidoAlmacen.getNumero() + " / "
                                        + pedidoAlmacen.getIdPedidoExterno());
                        // if (!pedidoAlmacen.getNumero().equals("N3375291-C-1R-1A")) continue;
                        ResultadoPublicacionPedidoM resultadoPedido = new ResultadoPublicacionPedidoM();
                        resultadoPedido.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                        resultadoPedido.setOrderNumberHermes(pedidoAlmacen.getNumero());
                        resultadoPedido.setOrderNumberChannel(pedidoAlmacen.getIdPedidoExterno());
                        try {
                                pedidoAlmacen = hermesCore.leerPedidoCompleto(pedidoAlmacen.getIdPedido());
                                // buscamos estado actual del pedido
                                String resultShipping = "";

                                for (PedidoLineaDto linea : pedidoAlmacen.getPedidoLineaList()) {

                                        String idPedidoOriginal = (String) linea
                                                        .getAtributo(MercadoLibreConstants.ATR_ORIGINAL_ORDER_ID);
                                        procesoInfo.addDescripcion(
                                                        "Checking status for order=" + pedidoAlmacen.getNumero() + " / "
                                                                        + pedidoAlmacen.getIdPedidoExterno());
                                        if (StringUtils.isEmpty(idPedidoOriginal)) {
                                                idPedidoOriginal = pedidoAlmacen.getIdPedidoExterno();
                                        }
                                        if (originalOrderIdsTratados.contains(idPedidoOriginal)) {
                                                procesoInfo.addDescripcion("Skipping multiple original order id "
                                                                + idPedidoOriginal);
                                                continue;
                                        } else {
                                                originalOrderIdsTratados.add(idPedidoOriginal);
                                        }
                                        procesoInfo.addDescripcion(
                                                        "Querying " + MercadoLibreConstants.ATR_ORIGINAL_ORDER_ID
                                                                        + " = " + idPedidoOriginal);

                                        JSONObject objectOrder = null;
                                        try {
                                                objectOrder = orderMercadoLibreConnector.getOrder(credentials, mercado,
                                                                idPedidoOriginal, procesoInfo);
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("", e);
                                                mercado = getMeli(credentials, true, idSite, procesoInfo);
                                                objectOrder = orderMercadoLibreConnector.getOrder(credentials, mercado,
                                                                idPedidoOriginal, procesoInfo);
                                        }

                                        Long idShipping = null;
                                        String packId = (objectOrder.has("pack_id")
                                                        && !StringUtils.isEmpty(objectOrder.getString("pack_id"))
                                                        && !"null".equalsIgnoreCase(objectOrder.getString("pack_id")))
                                                                        ? objectOrder.getString("pack_id")
                                                                        : objectOrder.getString("id");

                                        String user_id = objectOrder.getJSONObject("buyer").getString("id");

                                        /*
                                         * //procesoInfo.addDescripcion("MANDANDO 1");
                                         * sendMailML(credentials, mercado, idPedidoOriginal, pedidoAlmacen,
                                         * TypeTools.getCredential(credentials,
                                         * MercadoLibreConstants.MERCADO_MAIL_TEXTO_CREADO), procesoInfo,user_id);
                                         * procesoInfo.addDescripcion("MANDANDO 2");
                                         * Boolean mandados = sendMailML(credentials, mercado, idPedidoOriginal,
                                         * pedidoAlmacen, TypeTools.getCredential(credentials,
                                         * MercadoLibreConstants.MERCADO_MAIL_TEXTO_FACTURA), procesoInfo,user_id);
                                         * procesoInfo.addDescripcion("MANDANDO 3");
                                         * mandados = sendMailML(credentials, mercado, idPedidoOriginal, pedidoAlmacen,
                                         * TypeTools.getCredential(credentials,
                                         * MercadoLibreConstants.MERCADO_MAIL_TEXTO_ENVIANDO), procesoInfo,user_id);
                                         * procesoInfo.addDescripcion("MANDANDO 4");
                                         * mandados = sendMailML(credentials, mercado, idPedidoOriginal, pedidoAlmacen,
                                         * TypeTools.getCredential(credentials,
                                         * MercadoLibreConstants.MERCADO_MAIL_TEXTO_ENTREGADO), procesoInfo,user_id);
                                         * procesoInfo.addDescripcion("MANDANDO 5");
                                         * mandados = sendMailML(credentials, mercado, idPedidoOriginal,
                                         * pedidoAlmacen,TypeTools.getCredential(credentials,
                                         * MercadoLibreConstants.MERCADO_MAIL_TEXTO_ETIQUETA), procesoInfo, user_id);
                                         * procesoInfo.addDescripcion("++++++++++++++++++++++++++++MANDADOS TODOS");
                                         * if (true)continue;
                                         */
                                        // Mandar mail recien comprado
                                        String nombreAtributoMailCreado = MercadoLibreConstants.ATR_MAIL_CREADO;

                                        if (!TypeTools.isTrue(
                                                        (String) pedidoAlmacen.getAtributo(nombreAtributoMailCreado))) {
                                                Boolean mandado = sendMailML(credentials, mercado, idPedidoOriginal,
                                                                pedidoAlmacen, packId,
                                                                TypeTools.getCredential(credentials,
                                                                                MercadoLibreConstants.MERCADO_MAIL_TEXTO_CREADO),
                                                                procesoInfo, user_id, idShipping);
                                                pedidoAlmacen.setAtributo(nombreAtributoMailCreado, "" + mandado);
                                                hermesCore.saveAtributosPedido(pedidoAlmacen.getIdPedido(),
                                                                pedidoAlmacen.getAtributos());
                                        }
                                        JSONObject objectShipping = null;
                                        try {
                                                idShipping = Long.parseLong(
                                                                objectOrder.getJSONObject("shipping").getString("id"));
                                                objectShipping = shippingMercadoLivreConnector.get(mercado, idShipping,
                                                                procesoInfo);
                                        } catch (Exception e) {
                                                try {
                                                        procesoInfo.addDescripcion("", e);
                                                        mercado = getMeli(credentials, true, idSite, procesoInfo);
                                                        objectShipping = shippingMercadoLivreConnector.get(mercado,
                                                                        idShipping, procesoInfo);
                                                } catch (Exception casue) {
                                                        procesoInfo.addDescripcion("", e);
                                                }
                                        }

                                        if (objectShipping == null) {
                                                procesoInfo.addDescripcion(
                                                                getOrderNumber(pedidoAlmacen) + " shipping not found");
                                                continue;
                                        }
                                        String valida = TypeTools.getCredential(credentials, Credenciales.MERCADO_RUT);
                                        if (valida != null && valida.equalsIgnoreCase("SI")) {
                                                if (!pedidoAlmacen.getFacturaGenerada()) {
                                                        procesoInfo.addDescripcion(getOrderNumber(pedidoAlmacen)
                                                                        + " without invoice bill, skipping.");
                                                        continue;
                                                }
                                        }

                                        String validaTraspaso = TypeTools.getCredential(credentials,
                                                        Credenciales.MERCADO_VALIDA_TRASPASO);
                                        if (!StringUtils.isEmpty(validaTraspaso)) {
                                                String traspasoEmitido = (String) pedidoAlmacen
                                                                .getAtributo(validaTraspaso);
                                                if (StringUtils.isEmpty(traspasoEmitido)) {
                                                        procesoInfo.addDescripcion(
                                                                        "aun no se ha generado el traspado, no hacemos nada");
                                                        continue;
                                                }
                                        }

                                        String numeroEnvioOperador = "";
                                        String status = "";
                                        status = objectShipping.getString("status");
                                        procesoInfo.addDescripcion("status=" + status);
                                        procesoInfo.addDescripcion("idShipping=" + idShipping);
                                        procesoInfo.addDescripcion("status order=" + pedidoAlmacen.getIdEstadoPedido()
                                                        + " del pedido=" + pedidoAlmacen.getNumero());
                                        String codigoRespuestaOperador = (idShipping == null) ? null : "" + idShipping;
                                        numeroEnvioOperador = objectShipping.has("tracking_number")
                                                        ? objectShipping.getString("tracking_number")
                                                        : null;
                                        procesoInfo.addDescripcion("numeroEnvioOperador=" + numeroEnvioOperador);
                                        procesoInfo.addDescripcion("operator response code for "
                                                        + getOrderNumber(pedidoAlmacen) + " :" + codigoRespuestaOperador
                                                        + " tracking = " + numeroEnvioOperador);
                                        procesoInfo.addDescripcion("operator code for " + getOrderNumber(pedidoAlmacen)
                                                        + " :" + pedidoAlmacen.getCodigoRespuestaOperador());
                                        Long idEstadoPedido = null;
                                        if (status.equalsIgnoreCase("handling")
                                                        || (status.equalsIgnoreCase("ready_to_ship")
                                                                        && pedidoAlmacen.getIdEstadoPedido().equals(
                                                                                        PublicConstants.ESTADO_PEDIDO_FACTURADO_EN_POS))) {
                                                idEstadoPedido = PedidoConstants.ESTADO_PEDIDO_ACEPTADO_POR_OPERADOR;
                                                // Mandar mail facturado
                                                String nombreAtributoMail = MercadoLibreConstants.ATR_MAIL_FACTURADO;
                                                if (!TypeTools.isTrue((String) pedidoAlmacen
                                                                .getAtributo(nombreAtributoMail))) {
                                                        Boolean mandado = sendMailML(credentials, mercado,
                                                                        idPedidoOriginal, pedidoAlmacen, packId,
                                                                        TypeTools.getCredential(credentials,
                                                                                        MercadoLibreConstants.MERCADO_MAIL_TEXTO_FACTURA),
                                                                        procesoInfo, user_id, idShipping);
                                                        if (mandado) {
                                                                pedidoAlmacen.setAtributo(nombreAtributoMail,
                                                                                "" + mandado);
                                                                hermesCore.saveAtributosPedido(
                                                                                pedidoAlmacen.getIdPedido(),
                                                                                pedidoAlmacen.getAtributos());
                                                                if (mandado && pedidoAlmacen.getIdEstadoPedido().equals(
                                                                                PublicConstants.ESTADO_PEDIDO_FACTURADO_EN_POS)) {
                                                                        idEstadoPedido = PedidoConstants.ESTADO_PEDIDO_FACTURADO_EN_POS;
                                                                }
                                                        }
                                                } /*
                                                   * nombreAtributoMail=ATR_MAIL_ETIQUETA;
                                                   * if(!TypeTools.isTrue((String)pedidoAlmacen.getAtributo(
                                                   * nombreAtributoMail))){
                                                   * Boolean mandado = sendMailML(credentials, mercado,
                                                   * idPedidoOriginal,
                                                   * pedidoAlmacen,TypeTools.getCredential(credentials,
                                                   * "MERCADO_MAIL_TITULO_ETIQUETA"),
                                                   * TypeTools.getCredential(credentials,
                                                   * "MERCADO_MAIL_TEXTO_ETIQUETA"), procesoInfo);
                                                   * pedidoAlmacen.setAtributo(nombreAtributoMail, ""+mandado);
                                                   * hermesCore.saveAtributosPedido(pedidoAlmacen.getIdPedido(),
                                                   * pedidoAlmacen.getAtributos());
                                                   * }
                                                   */
                                        } else {
                                                if (status.equalsIgnoreCase("ready_to_ship")) {
                                                        idEstadoPedido = PedidoConstants.ESTADO_PEDIDO_ACEPTADO_POR_OPERADOR;

                                                }
                                        }
                                        if (status.equalsIgnoreCase("shipped")) {
                                                idEstadoPedido = PedidoConstants.ESTADO_PEDIDO_EN_TRANSITO;
                                                // Mandar mail enviado
                                                String nombreAtributoMail = MercadoLibreConstants.ATR_MAIL_ENVIADO;
                                                if (!TypeTools.isTrue(
                                                                (String) pedidoAlmacen.getAtributo(nombreAtributoMail))
                                                                && !StringUtils.isEmpty(codigoRespuestaOperador)
                                                                && !StringUtils.isEmpty(numeroEnvioOperador)
                                                                && !status.equals("cancelled")) {
                                                        pedidoAlmacen.setCodigoRespuestaOperador(
                                                                        codigoRespuestaOperador);
                                                        pedidoAlmacen.setNumeroEnvioOperador(numeroEnvioOperador);
                                                        // hermesCore.updatePedido(pedidoAlmacen);

                                                        Boolean mandado = sendMailML(credentials, mercado,
                                                                        idPedidoOriginal, pedidoAlmacen, packId,
                                                                        TypeTools.getCredential(credentials,
                                                                                        MercadoLibreConstants.MERCADO_MAIL_TEXTO_ENVIANDO),
                                                                        procesoInfo, user_id, idShipping);
                                                        if (mandado) {
                                                                pedidoAlmacen.setAtributo(nombreAtributoMail,
                                                                                "" + mandado);
                                                                hermesCore.saveAtributosPedido(
                                                                                pedidoAlmacen.getIdPedido(),
                                                                                pedidoAlmacen.getAtributos());
                                                        }

                                                }
                                        }

                                        if (status.equalsIgnoreCase("delivered")) {
                                                idEstadoPedido = PedidoConstants.ESTADO_PEDIDO_ENTREGADO;
                                                // Mandar mail entregado
                                                String nombreAtributoMail = MercadoLibreConstants.ATR_MAIL_ENTREGADO;
                                                if (!TypeTools.isTrue((String) pedidoAlmacen
                                                                .getAtributo(nombreAtributoMail))) {
                                                        Boolean mandado = sendMailML(credentials, mercado,
                                                                        idPedidoOriginal, pedidoAlmacen, packId,
                                                                        TypeTools.getCredential(credentials,
                                                                                        MercadoLibreConstants.MERCADO_MAIL_TEXTO_ENTREGADO),
                                                                        procesoInfo, user_id);
                                                        if (mandado) {
                                                                pedidoAlmacen.setAtributo(nombreAtributoMail,
                                                                                "" + mandado);
                                                                hermesCore.saveAtributosPedido(
                                                                                pedidoAlmacen.getIdPedido(),
                                                                                pedidoAlmacen.getAtributos());
                                                        }
                                                }
                                        }
                                        if (status.equalsIgnoreCase("not_delivered")) {
                                                idEstadoPedido = PedidoConstants.ESTADO_PEDIDO_INCIDENCIA_EN_ENTREGA_PREPARACION;
                                        }
                                        if (status.equalsIgnoreCase("cancelled")) {
                                                idEstadoPedido = PublicConstants.ESTADO_PEDIDO_ANULADO_EN_MARKETPLACE;
                                        }

                                        // Mandar mail facturado
                                        String nombreAtributoMail = MercadoLibreConstants.ATR_MAIL_FACTURADO;
                                        if (!TypeTools.isTrue((String) pedidoAlmacen.getAtributo(nombreAtributoMail))) {
                                                Boolean mandado = sendMailML(credentials, mercado, idPedidoOriginal,
                                                                pedidoAlmacen, packId,
                                                                TypeTools.getCredential(credentials,
                                                                                MercadoLibreConstants.MERCADO_MAIL_TEXTO_FACTURA),
                                                                procesoInfo, user_id, idShipping);
                                                if (mandado) {
                                                        pedidoAlmacen.setAtributo(nombreAtributoMail, "" + mandado);
                                                        hermesCore.saveAtributosPedido(pedidoAlmacen.getIdPedido(),
                                                                        pedidoAlmacen.getAtributos());
                                                }
                                        }

                                        if (idEstadoPedido == null) {
                                                continue;
                                        }

                                        procesoInfo.addDescripcion("CAMBIO DE ESTADO pedido "
                                                        + pedidoAlmacen.getNumero() + " codRespOp="
                                                        + pedidoAlmacen.getCodigoRespuestaOperador()
                                                        + " idEstadoPedido=" + pedidoAlmacen.getIdEstadoPedido()
                                                        + " IdEstadoLeido=" + idEstadoPedido + " status=" + status);

                                        PedidoEstadoHistoricoDto pedidoHistorico = new PedidoEstadoHistoricoDto(
                                                        pedidoAlmacen);
                                        if ((StringUtils.isEmpty(pedidoAlmacen.getNumeroEnvioOperador())
                                                        && !StringUtils.isEmpty(codigoRespuestaOperador)
                                                        && !StringUtils.isEmpty(numeroEnvioOperador)
                                                        && !status.equals("cancelled"))
                                                        || pedidoAlmacen.getIdEstadoPedido().equals(
                                                                        PublicConstants.ESTADO_PEDIDO_EN_PREPARACION)) {
                                                pedidoAlmacen.setCodigoRespuestaOperador(codigoRespuestaOperador);
                                                pedidoAlmacen.setNumeroEnvioOperador(numeroEnvioOperador);
                                                descargarGuia(credentials,
                                                                TypeTools.getCredential(credentials,
                                                                                Credenciales.MERCADO_LIBRE_ORDERID),
                                                                pedidoAlmacen, "pdf", mercado);
                                                // getDocuments(credentials, codigoRespuestaOperador, pedidoAlmacen,
                                                // mercado, "pdf");

                                                if (site.getIdErp() != null &&
                                                                (site.getIdErp().equals(
                                                                                PublicConstants.ID_ERP_TOTVS_MODA) ||
                                                                                site.getIdErp().equals(
                                                                                                PublicConstants.ID_ERP_TOTVS_WINTHOR))) {
                                                        // FIXME: temporarl hasta tener el microservicio de pedidos
                                                        String url = URL_HERMES_ADMIN + "pedido.do";
                                                        HashMap<String, String> params = new HashMap<String, String>();
                                                        params.put("usu", "cbricio");
                                                        params.put("pas", "cbricio2015");
                                                        params.put("metodo", "actualizarEstadoAjax");
                                                        params.put("idNuevoEstado", "" + idEstadoPedido);
                                                        params.put("idPedido", "" + pedidoAlmacen.getIdPedido());
                                                        params.put("enviarZeleris", "" + false);
                                                        params.put("nombreUsuario", "chm");

                                                        StringBuffer retorno = HttpClientTools.HTTPConnection(url,
                                                                        "GET", params, pedidoAlmacen.getIdSite());
                                                        procesoInfo.addDescripcion("retorno=" + retorno);
                                                        pedidoAlmacen.setIdEstadoPedido(idEstadoPedido);
                                                } else {
                                                        pedidoHistorico.setUsuario("chm");
                                                        pedidoHistorico.setDescripcion(
                                                                        "Invoice downloaded and status changed in ML");
                                                        pedidoHistorico.setIdEstadoPedido(idEstadoPedido);
                                                        hermesCore.updateEstadoPedido(pedidoHistorico);
                                                }

                                                // Mandar mail facturado
                                                nombreAtributoMail = MercadoLibreConstants.ATR_MAIL_ETIQUETA;
                                                if (!TypeTools.isTrue((String) pedidoAlmacen
                                                                .getAtributo(nombreAtributoMail))) {
                                                        Boolean mandado = sendMailML(credentials, mercado,
                                                                        idPedidoOriginal, pedidoAlmacen, packId,
                                                                        TypeTools.getCredential(credentials,
                                                                                        MercadoLibreConstants.MERCADO_MAIL_TEXTO_ETIQUETA),
                                                                        procesoInfo, user_id, idShipping);
                                                        pedidoAlmacen.setAtributo(nombreAtributoMail, "" + mandado);
                                                        hermesCore.saveAtributosPedido(pedidoAlmacen.getIdPedido(),
                                                                        pedidoAlmacen.getAtributos());
                                                }

                                        } else {
                                                if (!idEstadoPedido.equals(pedidoAlmacen.getIdEstadoPedido())) {
                                                        if (site.getIdErp() != null &&
                                                                        (site.getIdErp().equals(
                                                                                        PublicConstants.ID_ERP_TOTVS_MODA)
                                                                                        ||
                                                                                        site.getIdErp().equals(
                                                                                                        PublicConstants.ID_ERP_TOTVS_WINTHOR))) {
                                                                // FIXME: temporarl hasta tener el microservicio de
                                                                // pedidos
                                                                String url = URL_HERMES_ADMIN + "pedido.do";
                                                                HashMap<String, String> params = new HashMap<String, String>();
                                                                params.put("usu", "cbricio");
                                                                params.put("pas", "cbricio2015");
                                                                params.put("metodo", "actualizarEstadoAjax");
                                                                params.put("idNuevoEstado", "" + idEstadoPedido);
                                                                params.put("idPedido",
                                                                                "" + pedidoAlmacen.getIdPedido());
                                                                params.put("enviarZeleris", "" + false);
                                                                params.put("nombreUsuario", "chm");

                                                                StringBuffer retorno = HttpClientTools.HTTPConnection(
                                                                                url, "GET", params,
                                                                                pedidoAlmacen.getIdSite());
                                                                procesoInfo.addDescripcion("retorno=" + retorno);
                                                                pedidoAlmacen.setIdEstadoPedido(idEstadoPedido);
                                                        } else {
                                                                pedidoHistorico.setUsuario("chm");
                                                                pedidoHistorico.setDescripcion("Status changed in ML");
                                                                pedidoHistorico.setIdEstadoPedido(idEstadoPedido);
                                                                hermesCore.updateEstadoPedido(pedidoHistorico);
                                                        }

                                                } else {
                                                        continue;
                                                }
                                        }
                                        procesoInfo.addDescripcion("ORDER " + pedidoAlmacen.getIdPedidoExterno()
                                                        + " HERMES=" + pedidoAlmacen.getNumero() + " change="
                                                        + pedidoHistorico.getDescripcion());
                                        resultadoPedido.setOk(true);
                                        resultadoPedido.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                        publicationResultService.guardarResultadoPedido(resultadoPedido);
                                        resultadoPublicacion.addPedido(resultadoPedido);
                                        procesoInfo.addDescripcion(
                                                        "RESULT=" + BeanDescriber.stringify(resultadoPedido));
                                }
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                                procesoInfo.addDescripcion("ERROR " + getOrderNumber(pedidoAlmacen), e);
                                resultadoPedido.setOk(false);
                                resultadoPedido.setError(getOrderNumber(pedidoAlmacen) + " " + e.getMessage());
                                resultadoPedido.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                                publicationResultService.guardarResultadoPedido(resultadoPedido);
                                resultadoPublicacion.addPedido(resultadoPedido);
                                resultadoPublicacion.setCountError(resultadoPublicacion.getCountError() + 1);
                                monitor.alert(cmCanalSite.getIdCanal().getIdCanal(), cmCanalSite.getIdSiteRotulo(),
                                                "UpdateOrderStatus");
                        }
                }
                procesoInfo.setResultadoPublicacion(resultadoPublicacion);
                procesoInfo.addDescripcion("updateStatusOrder END");
                monitor.event(cmCanalSite.getIdCanal().getIdCanal(), cmCanalSite.getIdSiteRotulo(),
                                "UpdateOrderStatus");
                return;
        }

        private void descargarGuia(List<CredentialsSiteChannelDto> credentials, String callerId,
                        PedidoDto pedidoAlmacen, String formato,
                        Meli mercado) throws Exception {

                URL urlConn = new URL(
                                "https://api.mercadolibre.com/shipment_labels?access_token=" + mercado.getAccessToken()
                                                + "&shipment_ids=" + pedidoAlmacen.getCodigoRespuestaOperador()
                                                + "&response_type=" + formato
                                                + "&caller.id=" + callerId + "&seller=" + callerId);
                File file = new File(rutaGuias);
                if (!file.exists())
                        file.mkdirs();

                String localFile = rutaGuias + "guia_ML_" + pedidoAlmacen.getNumero() + ".pdf";
                URLConnection conn = urlConn.openConnection();

                conn.setRequestProperty("User-Agent",
                                "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
                conn.connect();
                InputStream in = conn.getInputStream();
                OutputStream out = new FileOutputStream(localFile);

                int b = 0;
                while (b != -1) {
                        b = in.read();
                        if (b != -1)
                                out.write(b);
                }
                out.close();
                in.close();
                String respuesta = hermesService.enviarXMlATienda(pedidoAlmacen.getIdSite(),
                                pedidoAlmacen.getIdPedido() + "",
                                pedidoAlmacen.getNumeroEnvioOperador(), localFile);
                if (!respuesta.equals("OK")) {
                        throw new Exception("Error send guia to store | " + respuesta);
                }

        }

        private CanalSiteProductoM buscarProducto(Long idProducto, CanalSiteBD cmCanalSite) {
                CanalSiteProductoM canalSiteProducto = null;
                List<CanalSiteProductoM> canalSiteProductoList = canalSiteProductoRepositoryM
                                .findAllByIdSiteCanalAndIdProducto(cmCanalSite.getIdCanalSite(), idProducto);
                if (canalSiteProductoList != null && !canalSiteProductoList.isEmpty()) {
                        canalSiteProducto = canalSiteProductoList.get(0);
                }
                return canalSiteProducto;
        }

        public ResultadoPublicacionProductoM insertItemsInternal(CanalSiteBD cmCanalSite,
                        List<CredentialsSiteChannelDto> credentials, ProductoCollectionDto producto,
                        String idSite, SiteDto site, Boolean precioLleno, String itemId, CencosudProvider cencosudProvider, String garantia,
                        ProcesoInfo procesoInfo, ResultadoPublicacionProductoM resultDetail)
                        throws Exception {
                procesoInfo.addDescripcion("insertItemsInternal INIT");
                procesoInfo.addDescripcion("insertItemsInternal " + getProductNumber(producto));

                // do login
                cencosudProvider.login()
                // String[] headerQueryParams = { "Content-Type", "Authorization", "access_token", "Accept" };
                // String[] headerQueryParamsValues = { "application/json; charset=UTF-8",
                //                 "Bearer " + mercado.getAccessToken(), mercado.getAccessToken(), "application/json" };

                try {
                        // String SITE_ML = TypeTools.getCredential(credentials, Credenciales.MERCADO_MELI_COUNTRY_CODE);
                        // FluentStringsMap params = new FluentStringsMap();
                        // JSONObject productoJson = new JSONObject();

                        CSProduct csProduct = new CSProduct();

                        procesoInfo.addDescripcion("NICK_NAME_CENCOSUD="
                                        + producto.getValorAtributoOrDefault("NICK_NAME_CENCOSUD", ""));
                        procesoInfo.addDescripcion("nick_name_cencosud="
                                        + producto.getValorAtributoOrDefault("nick_name_cencosud", ""));
                        // productoJson.put("title", titulo);
                        csProduct.setName(producto.getNombre());

                        CategoriaCanalBD categoria = categoryService.obtenerCategoriaMapeada(producto.getIdCategorias(),
                                        PublicConstants.CANAL_MERCADO_LIBRE_ID,
                                        cmCanalSite.getIdSiteRotulo());

                        if (categoria != null && categoria.getIdCategoriaEnCanal() != null) {
                                CSCategory csCategory = new CSCategory();
                                csCategory.setId(categoria.getIdCategoriaEnCanal());
                                
                                productoJson.put("category_id", categoria.getIdCategoriaEnCanal());
                        } else {
                                procesoInfo.addDescripcion("categorias del producto:" + producto.getIdCategorias());
                                throw new IntegrationExecuteException(
                                                "Categorías del producto (" + producto.getIdCategorias()
                                                                + ") no mapeadas con categorias principal del canal");
                        }

                        List<CategoriaCanalAtributoBD> attributes = getAttributes(mercado,
                                        categoria.getIdCategoriaCanal(),
                                        categoria.getIdCategoriaEnCanal(),
                                        procesoInfo);

                        productoJson.put("price", NumberTools.to(producto.getPrecio(), SITE_ML));

                        productoJson.put("currency_id",
                                        TypeTools.getCredential(credentials, Credenciales.MERCADO_CURRENCY));

                        if (!StringUtils.isEmpty(producto.getValorAtributoOrDefault("VIDEO_MERCADO", null))) {
                                productoJson.put("video_id", producto.getValorAtributoOrDefault("VIDEO_MERCADO", null));
                        }

                        String idOficinal = env.getProperty(
                                        "mercado.libre.official.store." + producto.getIdMarca() + "." + idSite);
                        if (StringUtils.isEmpty(idOficinal)) {
                                idOficinal = TypeTools.getCredential(credentials, Credenciales.MERCADO_OFICIAL_STORE);
                        }

                        if (site.getAgregacionRotulos()) {
                                String idOficinalAgrupado = env.getProperty("mercado.libre.official.store.agregador."
                                                + producto.getIdSite() + "." + idSite);
                                if (!StringUtils.isEmpty(idOficinalAgrupado)) {
                                        idOficinal = idOficinalAgrupado;
                                }
                        }
                        if (!StringUtils.isEmpty(idOficinal)) {
                                productoJson.put("official_store_id", idOficinal);
                        }

                        productoJson.put("buying_mode", "buy_it_now");
                        productoJson.put("condition", "new");
                        String descripcion = "";
                        String MERCADO_ATRIBUTOS_DESCRIPCION = TypeTools.getCredential(credentials,
                                        Credenciales.MERCADO_ATRIBUTOS_DESCRIPCION);
                        procesoInfo.addDescripcion("MERCADO_ATRIBUTOS_DESCRIPCION=" + MERCADO_ATRIBUTOS_DESCRIPCION);
                        if (!StringUtils.isEmpty(MERCADO_ATRIBUTOS_DESCRIPCION)) {
                                JSONObject atributos = JSONObject.fromObject(MERCADO_ATRIBUTOS_DESCRIPCION);
                                JSONArray arr = atributos.getJSONArray("atributos");
                                for (int i = 0; i < arr.size(); i++) {
                                        JSONObject oVariante = (JSONObject) arr.get(i);
                                        String nombre = oVariante.getString("nombre");
                                        String valor = oVariante.getString("valor");
                                        String valorAtributo = producto.getValorAtributoOrDefault(valor, "");
                                        if (!StringUtils.isEmpty(valorAtributo)) {
                                                descripcion = descripcion + "-" + nombre + ": " + valorAtributo + "\n";
                                        }
                                }

                        } else {

                                String mercadoAtributoDescrion = TypeTools.getCredential(credentials,
                                                Credenciales.MERCADO_ATRIBUTO_DESCRIPCION);
                                if (!StringUtils.isEmpty(mercadoAtributoDescrion)) {
                                        descripcion = producto.getValorAtributoOrDefault(mercadoAtributoDescrion, "");

                                        if (descripcion != null) {
                                                descripcion = StringEscapeUtils.escapeHtml(descripcion);
                                                procesoInfo.addDescripcion("description=" + descripcion);
                                        }
                                }

                                if (StringUtils.isEmpty(descripcion)) {
                                        String descripcionTecnica = producto
                                                        .getValorAtributoOrDefault("DESCRIPCION_TECNICA", "");
                                        if (StringUtils.isEmpty(descripcionTecnica)) {
                                                descripcion = producto.getValorAtributoOrDefault("ML_DescripcionLarga",
                                                                "");
                                                if (StringUtils.isEmpty(descripcion)) {
                                                        descripcion = producto.getValorAtributoOrDefault("DESCRIPCION",
                                                                        "");
                                                        if (descripcion != null) {
                                                                descripcion = StringEscapeUtils.escapeHtml(descripcion);
                                                                procesoInfo.addDescripcion(
                                                                                "description=" + descripcion);
                                                        }
                                                }
                                        } else {
                                                try {
                                                        if (TypeTools.isTrue(TypeTools.getCredential(credentials,
                                                                        Credenciales.MERCADO_DESCRIPCION_COMPLETA))) {
                                                                descripcion = producto.getValorAtributoOrDefault(
                                                                                "DESCRIPCION", "") + "\n\n";
                                                        }

                                                        String[] valores = descripcionTecnica.trim().split("\\|");
                                                        String[] valoresEstilo;
                                                        for (int i = 0; i < valores.length; i++) {
                                                                if (valores[i] != "" && !valores[i].isEmpty()) {
                                                                        valoresEstilo = valores[i].trim().split(":");
                                                                        try {
                                                                                if (valoresEstilo.length >= 2
                                                                                                && valoresEstilo[1] != ""
                                                                                                && !valoresEstilo[1]
                                                                                                                .isEmpty()) {
                                                                                        descripcion = descripcion + "-"
                                                                                                        + valoresEstilo[0]
                                                                                                        + ":"
                                                                                                        + valoresEstilo[1]
                                                                                                        + "\n";
                                                                                }
                                                                        } catch (Exception e) {
                                                                                procesoInfo.addDescripcion("", e);
                                                                        }
                                                                }
                                                        }
                                                } catch (Exception e) {
                                                        procesoInfo.addDescripcion("Error calculating ML description",
                                                                        e);
                                                        procesoInfo.addDescripcion("", e);
                                                }
                                        }
                                }
                        }
                        procesoInfo.addDescripcion("description=" + descripcion);
                        JSONObject descripcionJson = new JSONObject();
                        descripcionJson.put("plain_text", descripcion);
                        productoJson.put("description", descripcionJson);

                        String listing = TypeTools.getCredential(credentials, Credenciales.MERCADO_LISTING_TYPE);
                        if (StringUtils.isEmpty(listing)) {
                                listing = "bronze";
                        }
                        productoJson.put("listing_type_id", listing);
                        productoJson.put("seller_custom_field", producto.getIdArticuloModalia());

                        Integer numFotos = 1;
                        if (StringUtils.isNotEmpty(producto.getValorAtributoOrDefault("NUMERO_FOTOS", null))) {
                                numFotos = Integer.parseInt(producto.getValorAtributoOrDefault("NUMERO_FOTOS", null));
                        }
                        procesoInfo.addDescripcion("Number of pictures" + numFotos);
                        if (numFotos == 0)
                                numFotos = 3;
                        JSONArray imagenes = new JSONArray();
                        String urlsImagen = baseImages + "/" + producto.getIdSite() + "/"
                                        + producto.getCodigoAlfa() + "/";

                        for (int i = 1; i <= numFotos; i++) {
                                JSONObject productoimagenJson = new JSONObject();
                                productoimagenJson.put("source", urlsImagen + i + "-Z.jpg");
                                imagenes.add(productoimagenJson);
                        }
                        procesoInfo.addDescripcion("Number of pictures" + numFotos);

                        productoJson.put("pictures", imagenes);

                        JSONObject shippingJson = new JSONObject();
                        shippingJson.put("mode", "me2");
                        /*
                         * if (producto.getCodigoAlfa().equals("P311439_GU8")) {
                         * mercado.post("/sites/" + SITE_ML + "/shipping/selfservice/items/" + itemId ,
                         * params,"");
                         * }
                         */
                        String activarRetire = TypeTools.getCredential(credentials,
                                        Credenciales.MERCADO_ACTIVAR_RETIRO);
                        if (activarRetire == null || !activarRetire.equalsIgnoreCase("SI")) {
                                shippingJson.put("local_pick_up", false);
                        } else {
                                shippingJson.put("local_pick_up", true);
                        }
                        String gastosGratis = TypeTools.getCredential(credentials, Credenciales.MERCADO_ENVIOS_GRATIS);
                        String politicaEnvio = leerPoliticaDelModeloDeEnvio(
                                        procesoInfo.getResultadoPublicacion().getIdPublicacion(), "",
                                        ID_ATR_ENVIO_POLITICA_ENVIO, procesoInfo);
                        if (!StringUtils.isEmpty(politicaEnvio) && politicaEnvio.equals("free_shipping")) {
                                shippingJson.put("free_shipping", true);
                        } else {
                                if (gastosGratis == null || !gastosGratis.equalsIgnoreCase("SI")) {

                                        shippingJson.put("free_shipping", false);
                                } else {
                                        shippingJson.put("free_shipping", true);
                                }
                        }
                        productoJson.put("shipping", shippingJson);

                        JSONArray atributos = new JSONArray();
                        Boolean tieneForwarType = false;
                        Boolean tieneGuia = false;
                        Map<String, String> atributosCanal = categoryService
                                        .obtenerAtributosMapeadosCategoriaCanal(categoria, producto, false, false);
                        procesoInfo.addDescripcion(
                                        "atributos [" + categoria.getIdCategoriaEnCanal() + "] " + atributosCanal);
                        Set<Entry<String, String>> lineasGenerales = atributosCanal.entrySet();
                        Iterator<Entry<String, String>> itGenerales = lineasGenerales.iterator();
                        while (itGenerales.hasNext()) {
                                Entry<String, String> lineaGeneral = itGenerales.next();
                                procesoInfo.addDescripcion(lineaGeneral.getKey() + "-" + lineaGeneral.getValue());

                                if (!StringUtils.isEmpty(lineaGeneral.getValue())) {
                                        JSONObject atributoMarca = new JSONObject();
                                        atributoMarca.put("id", lineaGeneral.getKey());
                                        atributoMarca.put("name", mapAtributos.get(lineaGeneral.getKey()));
                                        atributoMarca.put("value_name", lineaGeneral.getValue());
                                        atributos.add(atributoMarca);

                                        if (lineaGeneral.getKey().equals("FOOTWEAR_TYPE")) {
                                                tieneForwarType = true;
                                        }
                                        if (lineaGeneral.getKey().equals("SIZE_GRID_ID")) {
                                                tieneGuia = true;
                                        }
                                }

                        }

                        String guia = env.getProperty(
                                        "mercado.libre.guia." + site.getIdSite() + "." + producto.getIdMarca());
                        if (!tieneGuia && !StringUtils.isEmpty(guia)) {
                                JSONObject atributoMarca = new JSONObject();
                                atributoMarca.put("id", "SIZE_GRID_ID");
                                atributoMarca.put("name", "ID de la guía de talles");
                                atributoMarca.put("value_name", guia);
                                atributos.add(atributoMarca);
                        }
                        // FIXME: nyapa para totvs hasta que tengamosla nueva estion de atribugos
                        // if (attributes.stream().anyMatch(attribute -> attribute.is("SIZE"))) {
                        // if (!site.isTOTVSWinThorERP()) {
                        if (hasVariation(site, procesoInfo, credentials, producto)) {
                                procesoInfo.addDescripcion("Variation flow.");
                                JSONArray variations = variations(credentials, producto, resultDetail, numFotos, true,
                                                procesoInfo, SITE_ML);
                                productoJson.put("variations", variations);
                        } else {
                                procesoInfo.addDescripcion("No variation flow.");
                                Long stock = getStockProducto(producto);
                                if (!producto.getActivo()) {
                                        stock = 0L;
                                }
                                productoJson.put("available_quantity", stock);
                                productoJson.put("price", NumberTools.to(producto.getPrecio(), SITE_ML));

                                JSONObject atributoGtin = new JSONObject();
                                atributoGtin.put("id", "GTIN");
                                atributoGtin.put("value_name", getEanProducto(producto));
                                atributos.add(atributoGtin);

                        }

                        JSONObject atributoMarca = new JSONObject();
                        atributoMarca.put("id", "BRAND");
                        atributoMarca.put("name", "Marca");
                        atributoMarca.put("value_name", producto.getMarca());
                        atributos.add(atributoMarca);
                        JSONObject atributoGenero = new JSONObject();
                        atributoGenero.put("id", "GENDER");
                        atributoGenero.put("name", "Género");
                        String moneda = TypeTools.getCredential(credentials, Credenciales.MERCADO_CURRENCY);
                        if (moneda != null && moneda.equalsIgnoreCase("BRL")) {
                                atributoGenero.put("value_name",
                                                MercadoLibreConstants.mapaGenerosBR.get(producto.getGenero()));
                        } else {
                                atributoGenero.put("value_name",
                                                MercadoLibreConstants.mapaGeneros.get(producto.getGenero()));
                        }
                        atributos.add(atributoGenero);
                        JSONObject atributoModelo = new JSONObject();
                        atributoModelo.put("id", "MODEL");
                        atributoModelo.put("name", "Modelo");
                        atributoModelo.put("value_name", producto.getValorAtributoOrDefault("MODELO", ""));
                        atributos.add(atributoModelo);

                        String composicion = producto.getValorAtributoOrDefault("COMPOSICION", "");
                        if (!StringUtils.isEmpty(composicion)) {
                                JSONObject atributo = new JSONObject();
                                atributo.put("id", "COMPOSITION");
                                atributo.put("name", "Composition");
                                atributo.put("value_name", StringTools.limita(composicion, MAXIMUM_COMPOSICION_SIZE));
                                atributos.add(atributo);
                        }
                        if (!StringUtils.isEmpty(producto.getValorAtributoOrDefault("MANUFACTURER", ""))) {
                                JSONObject atributoFabricante = new JSONObject();
                                atributoFabricante.put("id", "MANUFACTURER");
                                atributoFabricante.put("name", "Fabricante");
                                atributoFabricante.put("value_name",
                                                producto.getValorAtributoOrDefault("MANUFACTURER", ""));
                                atributos.add(atributoFabricante);
                        }
                        if (!StringUtils.isEmpty(producto.getValorAtributoOrDefault("TIPO_PRODUCTO_ML", ""))) {
                                JSONObject atributoFabricante = new JSONObject();
                                atributoFabricante.put("id", "GARMENT_TYPE");
                                atributoFabricante.put("name", "Tipo de producto");
                                atributoFabricante.put("value_name",
                                                producto.getValorAtributoOrDefault("TIPO_PRODUCTO_ML", ""));
                                atributos.add(atributoFabricante);
                        }
                        if (!StringUtils.isEmpty(producto.getValorAtributoOrDefault("ES_DEPORTIVO_ML", ""))) {
                                JSONObject atributoFabricante = new JSONObject();
                                atributoFabricante.put("id", "IS_SPORTIVE");
                                atributoFabricante.put("name", "Es deportivo");
                                atributoFabricante.put("value_name",
                                                producto.getValorAtributoOrDefault("ES_DEPORTIVO_ML", ""));
                                atributos.add(atributoFabricante);
                        }
                        if (!StringUtils.isEmpty(producto.getValorAtributoOrDefault("TIPO_PANTALON_ML", ""))) {
                                JSONObject atributoFabricante = new JSONObject();
                                atributoFabricante.put("id", "PANT_TYPE");
                                atributoFabricante.put("name", "Tipo de pantalón");
                                atributoFabricante.put("value_name",
                                                producto.getValorAtributoOrDefault("TIPO_PANTALON_ML", ""));
                                atributos.add(atributoFabricante);
                        }
                        if (!tieneForwarType
                                        && !StringUtils.isEmpty(producto.getValorAtributoOrDefault("Subfamilia", ""))) {
                                JSONObject atributoFabricante = new JSONObject();
                                atributoFabricante.put("id", "FOOTWEAR_TYPE");
                                atributoFabricante.put("name", "Tipo de calzado");
                                atributoFabricante.put("value_name",
                                                producto.getValorAtributoOrDefault("Subfamilia", ""));
                                atributos.add(atributoFabricante);
                        }

                        String material = producto.getMaterial();
                        if (!StringUtils.isEmpty(material)) {
                                JSONObject atributo = new JSONObject();
                                atributo.put("id", "MAIN_MATERIAL");
                                atributo.put("name", "Material");
                                atributo.put("value_name", producto.getMaterial());
                                atributos.add(atributo);
                        }
                        if (moneda != null && moneda.equalsIgnoreCase("BRL")) {
                                JSONObject atributo = new JSONObject();
                                atributo = new JSONObject();
                                atributo.put("id", "UNITS_PER_PACK");
                                atributo.put("name", "Unidades por kit");
                                atributo.put("value_name", "1");
                                atributos.add(atributo);

                                atributo = new JSONObject();
                                atributo.put("id", "SALE_FORMAT");
                                atributo.put("name", "Formato de venda");
                                atributo.put("value_name", "Unidade");
                                atributos.add(atributo);

                                atributo = new JSONObject();
                                atributo.put("id", "UNITS_PER_PACKAGE");
                                atributo.put("name", "Unidades por pacote");
                                atributo.put("value_name", "1");
                                atributos.add(atributo);
                        }
                        productoJson.put("attributes", atributos);

                        params.add("access_token", mercado.getAccessToken());

                        ResponseEntity<String> res = null;
                        String respuesta = "";
                        try {
                                res = HttpClientRestTools.callRestPost("https://api.mercadolibre.com/items",
                                                String.class,
                                                null,
                                                null,
                                                headerQueryParams,
                                                headerQueryParamsValues, productoJson.toString(), procesoInfo);
                                respuesta = res.getBody();
                        } catch (Exception e) {
                                respuesta = e.toString();
                        }

                        // Response r;
                        procesoInfo.addDescripcion("######O###### JSON REQUEST: " + productoJson.toString());
                        // r = mercado.post("/items", params, productoJson.toString());

                        procesoInfo.addDescripcion("######O###### JSON RESPONSE: " + respuesta);
                        try {
                                String error = "";
                                JSONObject jObject = JSONObject.fromObject(respuesta); // json
                                String seller_id = null;
                                String status = null;
                                String message = null;
                                String errorMsg = null;

                                try {
                                        seller_id = jObject.getString("id");
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                }
                                try {
                                        status = jObject.getString("status");
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                }
                                try {
                                        message = jObject.getString("message");
                                } catch (Exception e) {
                                }
                                try {
                                        errorMsg = jObject.getString("error");
                                } catch (Exception e) {
                                }

                                procesoInfo.addDescripcion("seller_id=" + seller_id);
                                procesoInfo.addDescripcion("status=" + status);
                                procesoInfo.addDescripcion("message=" + message);
                                procesoInfo.addDescripcion("error=" + errorMsg);

                                if (StringUtils.isEmpty(seller_id)) {
                                        resultDetail.setOk(false);
                                        resultDetail.setError(respuesta);
                                } else {
                                        try {
                                                JSONArray array = jObject.getJSONArray("variations");
                                                for (int i = 0; i < array.size(); i++) {
                                                        JSONObject oVariante = (JSONObject) array.get(i);
                                                        String seller_custom_field = oVariante
                                                                        .getString("seller_custom_field");
                                                        procesoInfo.addDescripcion(
                                                                        "seller_custom_field=" + seller_custom_field);
                                                        String id = null;
                                                        try {
                                                                id = oVariante.getString("id");
                                                        } catch (Exception e) {
                                                                Long idL = oVariante.getLong("id");
                                                                id = idL + "";
                                                        }
                                                        procesoInfo.addDescripcion("id=" + id);
                                                        List<ResultadoPublicacionProductoVariantM> variantes = resultDetail
                                                                        .getVariants();
                                                        for (ResultadoPublicacionProductoVariantM varian : variantes) {
                                                                if (varian.getSkuHermesChannel()
                                                                                .equals(seller_custom_field)) {
                                                                        varian.setIdItemChannel(id);
                                                                }
                                                        }
                                                }
                                                resultDetail.setOk(true);
                                                resultDetail.setError("");
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("", e);
                                                procesoInfo.addDescripcion("", e);
                                                resultDetail.setOk(false);
                                                resultDetail.setError(e.toString());
                                        }
                                }

                                resultDetail.setIdItemChannel(seller_id);
                                resultDetail.setSkuHermesChannel(producto.getIdArticuloModalia());

                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                                procesoInfo.addDescripcion(
                                                "####O##### Error storing " + getProductNumber(producto) + " :", e);
                                resultDetail.setOk(false);
                                resultDetail.setError(e.toString());
                        }
                } catch (Exception e) {
                        procesoInfo.addDescripcion("", e);
                        procesoInfo.addDescripcion("##########o######### Error in general process", e);
                        resultDetail.setError(e.toString());
                        resultDetail.setOk(false);
                }
                procesoInfo.addDescripcion("insertItemsInternal END");
                return resultDetail;
        }

        private List<CategoriaCanalAtributoBD> getAttributes(Meli mercado, Long idSiteRotulo,
                        List<Long> idCategorias,
                        ProcesoInfo procesoInfo) throws Exception {
                CategoriaCanalBD categoria = categoryService.obtenerCategoriaMapeada(idCategorias,
                                PublicConstants.CANAL_MERCADO_LIBRE_ID,
                                idSiteRotulo);

                if (categoria == null) {
                        procesoInfo.addDescripcion("categorias del producto:" + idCategorias);
                        throw new IntegrationExecuteException("Categorías del producto (" + idCategorias
                                        + ") no mapeadas con categorias principal del canal");
                }

                return getAttributes(mercado, categoria.getIdCategoriaCanal(),
                                categoria.getIdCategoriaEnCanal(), procesoInfo);
        }

        @Cacheable(value = "ml_attributes", key = "{#idCategoriaCanal, #idCategoriaEnCanal}", cacheManager = "cacheManager")
        public List<CategoriaCanalAtributoBD> getAttributes(Meli mercado, Long idCategoriaCanal,
                        String idCategoriaEnCanal,
                        ProcesoInfo procesoInfo) throws Exception {
                List<CategoriaCanalAtributoBD> attributes = categoriaCanalAtributoRepository
                                .findByIdCategoriaCanal(idCategoriaCanal);

                // if (attributes.isEmpty()) {
                //         attributes = (List<CategoriaCanalAtributoBD>) categoryMercadoLibreConnector
                //                         .getAttributes(mercado, idCategoriaEnCanal, procesoInfo)
                //                         .stream()
                //                         .map(OmniAttributesTransformer.create(idCategoriaCanal))
                //                         .collect(Collectors.toList());

                //         categoriaCanalAtributoRepository.save(attributes);
                // }

                return attributes;
        }

        private JSONArray variations(List<CredentialsSiteChannelDto> credentials, ProductoCollectionDto producto,
                        ResultadoPublicacionProductoM resultDetail, Integer numFotos,
                        boolean isInsert, ProcesoInfo procesoInfo, String country) {
                JSONArray variations = new JSONArray();

                List<ResultadoPublicacionProductoVariantM> resultsSub = new ArrayList<>();

                String urlsImagen = baseImages + "/" + producto.getIdSite() + "/"
                                + producto.getCodigoAlfa() + "/";

                Long stockTotal = 0L;
                String moneda = TypeTools.getCredential(credentials, Credenciales.MERCADO_CURRENCY);
                // buscamos subproductos con stock
                for (SubProductoDto subProducto : producto.getSubProductos()) {
                        if ((isInsert && subProducto.getStockEnSite() > 0) || !isInsert) {

                                ResultadoPublicacionProductoVariantM variant = new ResultadoPublicacionProductoVariantM();
                                JSONObject productoVariationJson = new JSONObject();
                                String atr = (String) subProducto.getAtributoStr("ID_ITEM_ML");
                                procesoInfo.addDescripcion("subProducto=" + subProducto.getValor() + "- IdML=" + atr
                                                + "- stock=" + subProducto.getStockEnSite());
                                if (!isInsert && !StringUtils.isEmpty(atr)) {
                                        procesoInfo.addDescripcion(
                                                        "hemos encontrado producto, le ponemos el id=" + atr);
                                        productoVariationJson.put("id", atr);

                                        JSONArray attribute_combinations = new JSONArray();
                                        JSONObject attributeCombinationJson = new JSONObject();
                                        attributeCombinationJson.put("id", "SIZE");
                                        attributeCombinationJson.put("name", "Talle");
                                        // subProducto.getTallaEu()
                                        attributeCombinationJson.put("value_name", subProducto.getValor());
                                        attribute_combinations.add(attributeCombinationJson);
                                        productoVariationJson.put("attribute_combinations", attribute_combinations);
                                } else {
                                        if (subProducto.getStockEnSite() <= 0)
                                                continue;

                                        JSONArray attribute_combinations = new JSONArray();
                                        JSONObject attributeCombinationJson = new JSONObject();
                                        attributeCombinationJson.put("id", "SIZE");
                                        attributeCombinationJson.put("name", "Talle");

                                        attributeCombinationJson.put("value_name", subProducto.getValor());
                                        attribute_combinations.add(attributeCombinationJson);

                                        attributeCombinationJson = new JSONObject();

                                        if (moneda != null && moneda.equalsIgnoreCase("BRL")) {
                                                String colorPaleta = producto.getColor().toUpperCase();
                                                colorPaleta = coloresBrasil.get(colorPaleta);
                                                if (StringUtils.isEmpty(colorPaleta)) {
                                                        attributeCombinationJson.put("value_name", producto.getColor());
                                                } else {
                                                        attributeCombinationJson.put("value_name", colorPaleta);
                                                }
                                        } else {
                                                attributeCombinationJson.put("value_name", producto.getColor());
                                        }
                                        attributeCombinationJson.put("id", "COLOR");
                                        attributeCombinationJson.put("name", "Color");
                                        attribute_combinations.add(attributeCombinationJson);

                                        productoVariationJson.put("attribute_combinations", attribute_combinations);
                                }

                                if ("BRL".equalsIgnoreCase(moneda)) {
                                        if (!StringUtils.isEmpty(subProducto.getEan())) {
                                                JSONArray gtinAttributes = new JSONArray();
                                                JSONObject attribute = new JSONObject();
                                                attribute.put("id", "GTIN");
                                                attribute.put("value_name", subProducto.getEan());
                                                gtinAttributes.add(attribute);
                                                productoVariationJson.put("attributes", gtinAttributes);
                                        }
                                }

                                Integer stock = subProducto.getStockEnSite().intValue();
                                try {
                                        if (stock == null || stock < 0 || !producto.getActivo()) {
                                                stock = 0;
                                        }
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("########O######### Error: ", e);
                                        if (stock == null || stock < 0) {
                                                stock = 0;
                                        }
                                }
                                stockTotal += stock;
                                productoVariationJson.put("available_quantity", stock);
                                if (isInsert) {
                                        productoVariationJson.put("price",
                                                        NumberTools.to(producto.getPrecio(), country));
                                } else {
                                        if (StringUtils.isEmpty(atr)) {
                                                productoVariationJson.put("price",
                                                                NumberTools.to(producto.getPrecioRebajado(), country));
                                        }

                                }

                                productoVariationJson.put("seller_custom_field", subProducto.getIdArticuloModalia());

                                if (numFotos == 0)
                                        numFotos = 3;
                                JSONArray imagenes = new JSONArray();
                                for (int i = 1; i <= numFotos; i++) {
                                        imagenes.add(urlsImagen + i + "-Z.jpg");
                                }

                                productoVariationJson.put("picture_ids", imagenes);
                                procesoInfo.addDescripcion("productoVariationJson=" + productoVariationJson.toString());
                                variations.add(productoVariationJson);

                                variant.setStock(subProducto.getStockEnSite() + "");
                                variant.setEan(subProducto.getEan());
                                // subProducto.getTallaEu()
                                try {
                                        variant.setSize(subProducto.getTallajeOriginalCliente());
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                }
                                // subProducto.getTalla()
                                variant.setSkuHermesChannel(subProducto.getIdArticuloModalia());
                                variant.setIdSubProducto(subProducto.getIdSubProducto());
                                variant.setStock(stock + "");
                                resultsSub.add(variant);
                        }
                }

                resultDetail.setVariants(resultsSub);
                return variations;
        }

        private String getIdProductoML(SiteDto site, CanalSiteBD cmCanalSite, ProductoCollectionDto product) {
                CanalSiteProductoM cmProducto = canalSiteProductoRepositoryM
                                .findOneByIdSiteCanalAndIdProductoAndPublicado(
                                                cmCanalSite.getIdCanalSite(), product.getIdProducto(), true);
                if (cmProducto == null) {
                        return null;
                }
                return cmProducto.getIdItemChannel();
        }

        private String getInfoColor(ProductoCollectionDto producto, List<CredentialsSiteChannelDto> credentials,
                        @SuppressWarnings("rawtypes") ProcesoInfo procesoInfo) {
                String result = "";
                String colorAtributo = TypeTools.getCredential(credentials, Credenciales.MERCADO_ATRIBUTO_COLOR);

                if ("DESCRIPCION_TECNICA".equals(colorAtributo)) {
                        String descripcionTecnica = producto.getValorAtributoOrDefault("DESCRIPCION_TECNICA", "");
                        String[] valores = descripcionTecnica.trim().split("\\|");
                        String[] valoresEstilo;
                        for (int i = 0; i < valores.length; i++) {
                                if (valores[i] != "" && !valores[i].isEmpty()) {
                                        valoresEstilo = valores[i].trim().split(":");
                                        try {
                                                if (valoresEstilo.length >= 2 && valoresEstilo[1] != ""
                                                                && !valoresEstilo[1].isEmpty() && valoresEstilo[0] != ""
                                                                && !valoresEstilo[0].isEmpty()) {
                                                        if (valoresEstilo[0].equalsIgnoreCase("COLOR")) {
                                                                result = valoresEstilo[1];
                                                                break;
                                                        }
                                                }
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("", e);
                                        }
                                }
                        }
                } else {
                        result = producto.getValorAtributoOrDefault(colorAtributo, "");
                }
                if (StringUtils.isEmpty(result)) {
                        result = producto.getColor();
                }
                return result;
        }

        public ResultadoPublicacionProductoM updateItemsInternal(CanalSiteBD cmCanalSite,
                        ProductoCollectionDto producto, String idSite, SiteDto site,
                        Boolean precioLleno, String itemId, Meli mercado, String garantia,
                        List<CredentialsSiteChannelDto> credentials, ResultadoPublicacionProductoM resultDetalle,
                        @SuppressWarnings("rawtypes") ProcesoInfo procesoInfo, List<AlmacenDto> almacenes)
                        throws Exception {

                String result = "";
                String SITE_ML = TypeTools.getCredential(credentials, Credenciales.MERCADO_MELI_COUNTRY_CODE);
                try {

                        procesoInfo.addDescripcion("item=" + itemId);
                        FluentStringsMap params = new FluentStringsMap();
                        JSONObject productoJson = new JSONObject();

                        Integer numFotos = 1;
                        if (StringUtils.isNotEmpty(producto.getValorAtributoOrDefault("NUMERO_FOTOS", null))) {
                                numFotos = Integer.parseInt(producto.getValorAtributoOrDefault("NUMERO_FOTOS", "0"));
                        }

                        procesoInfo.addDescripcion("numero fotos " + numFotos);
                        if (numFotos == 0)
                                numFotos = 3;
                        JSONArray imagenes = new JSONArray();
                        String urlsImagen = baseImages + "/" + producto.getIdSite() + "/"
                                        + producto.getCodigoAlfa() + "/";

                        for (int i = 1; i <= numFotos; i++) {
                                JSONObject productoimagenJson = new JSONObject();
                                productoimagenJson.put("source", urlsImagen + i + "-Z.jpg");
                                imagenes.add(productoimagenJson);
                        }

                        // productoJson.put("warranty", garantia);

                        procesoInfo.addDescripcion("ML_DescripcionLarga="
                                        + producto.getValorAtributoOrDefault("ML_DescripcionLarga", ""));
                        String descripcion = "";
                        String MERCADO_ATRIBUTOS_DESCRIPCION = TypeTools.getCredential(credentials,
                                        Credenciales.MERCADO_ATRIBUTOS_DESCRIPCION);
                        procesoInfo.addDescripcion("MERCADO_ATRIBUTOS_DESCRIPCION=" + MERCADO_ATRIBUTOS_DESCRIPCION);
                        if (!StringUtils.isEmpty(MERCADO_ATRIBUTOS_DESCRIPCION)) {
                                JSONObject atributos = JSONObject.fromObject(MERCADO_ATRIBUTOS_DESCRIPCION);
                                JSONArray arr = atributos.getJSONArray("atributos");
                                for (int i = 0; i < arr.size(); i++) {
                                        JSONObject oVariante = (JSONObject) arr.get(i);
                                        String nombre = oVariante.getString("nombre");
                                        String valor = oVariante.getString("valor");
                                        String valorAtributo = producto.getValorAtributoOrDefault(valor, "");
                                        if (!StringUtils.isEmpty(valorAtributo)) {
                                                descripcion = descripcion + "-" + nombre + ": " + valorAtributo + "\n";
                                        }
                                }

                        } else {

                                String mercadoAtributoDescrion = TypeTools.getCredential(credentials,
                                                Credenciales.MERCADO_ATRIBUTO_DESCRIPCION);
                                if (!StringUtils.isEmpty(mercadoAtributoDescrion)) {
                                        descripcion = producto.getValorAtributoOrDefault(mercadoAtributoDescrion, "");
                                        if (descripcion != null) {
                                                descripcion = StringEscapeUtils.escapeHtml(descripcion);
                                                procesoInfo.addDescripcion("description=" + descripcion);
                                        }
                                }

                                if (StringUtils.isEmpty(descripcion)) {
                                        String descripcionTecnica = producto
                                                        .getValorAtributoOrDefault("DESCRIPCION_TECNICA", "");
                                        if (StringUtils.isEmpty(descripcionTecnica)) {
                                                descripcion = producto.getValorAtributoOrDefault("ML_DescripcionLarga",
                                                                "");
                                                if (StringUtils.isEmpty(descripcion)) {
                                                        descripcion = producto.getValorAtributoOrDefault("DESCRIPCION",
                                                                        "");
                                                }
                                        } else {
                                                try {
                                                        if (TypeTools.isTrue(TypeTools.getCredential(credentials,
                                                                        Credenciales.MERCADO_DESCRIPCION_COMPLETA))) {
                                                                descripcion = producto.getValorAtributoOrDefault(
                                                                                "DESCRIPCION", "") + "\n\n";
                                                        }

                                                        String[] valores = descripcionTecnica.trim().split("\\|");
                                                        String[] valoresEstilo;
                                                        for (int i = 0; i < valores.length; i++) {
                                                                if (valores[i] != "" && !valores[i].isEmpty()) {
                                                                        valoresEstilo = valores[i].trim().split(":");
                                                                        try {
                                                                                if (valoresEstilo.length >= 2
                                                                                                && valoresEstilo[1] != ""
                                                                                                && !valoresEstilo[1]
                                                                                                                .isEmpty()) {
                                                                                        descripcion = descripcion + "-"
                                                                                                        + valoresEstilo[0]
                                                                                                        + ":"
                                                                                                        + valoresEstilo[1]
                                                                                                        + "\n";
                                                                                }
                                                                        } catch (Exception e) {
                                                                                procesoInfo.addDescripcion("", e);
                                                                        }
                                                                }
                                                        }
                                                } catch (Exception e) {
                                                        procesoInfo.addDescripcion("Error calculating ML description",
                                                                        e);
                                                        procesoInfo.addDescripcion("", e);
                                                }
                                        }
                                }
                        }
                        procesoInfo.addDescripcion("description=" + descripcion);
                        JSONObject descripcionJson = new JSONObject();
                        descripcionJson.put("plain_text", descripcion);
                        // productoJson.put("description", descripcionJson);
                        procesoInfo.addDescripcion("NICK_NAME_CENCOSUD="
                                        + producto.getValorAtributoOrDefault("NICK_NAME_CENCOSUD", ""));
                        String titulo = "";
                        if (!StringUtils.isEmpty(producto.getValorAtributoOrDefault("NICK_NAME_CENCOSUD", ""))) {
                                titulo = producto.getValorAtributoOrDefault("NICK_NAME_CENCOSUD", "")
                                                .replaceAll("-", "");

                        } else {
                                String tituloML = env.getProperty("mercado.libre.title." + site.getIdSite());
                                if (!StringUtils.isEmpty(tituloML)) {
                                        StringTokenizer str = new StringTokenizer(tituloML, "|");
                                        while (str.hasMoreTokens()) {

                                                String atributo = str.nextToken();
                                                log.info(atributo);
                                                if (atributo.equalsIgnoreCase("CATEGORIA")) {
                                                        String categoriaPropia = hermesCore.getArbolCategoriasSite(
                                                                        producto.getIdProducto(), site.getIdSite());
                                                        if (!StringUtils.isEmpty(categoriaPropia)) {
                                                                titulo = titulo + " " + categoriaPropia;
                                                        }
                                                        log.info(atributo + "-" + categoriaPropia);
                                                } else {
                                                        if (atributo.equals("GENERO")) {
                                                                titulo = titulo + " " + producto.getGenero();
                                                        } else {
                                                                if (atributo.equals("NOMBRE_COLOR")) {
                                                                        titulo = titulo + " " + producto
                                                                                        .getDescripcionColor();
                                                                } else {
                                                                        String valor = producto
                                                                                        .getValorAtributoOrDefault(
                                                                                                        atributo, "");
                                                                        if (!StringUtils.isEmpty(valor)) {
                                                                                titulo = titulo + " " + valor;
                                                                        }
                                                                }
                                                        }
                                                }

                                        }
                                        // productoJson.put("title", titulo);
                                } else {
                                        if (!StringUtils.isEmpty(producto.getValorAtributoOrDefault("NICK_NAME", ""))) {
                                                titulo = producto.getValorAtributoOrDefault("NICK_NAME", "");
                                        } else {
                                                titulo = producto.getIdPmm();
                                        }
                                }
                        }
                        if (titulo.length() > 60) {
                                titulo = titulo.substring(0, 59);
                        }
                        productoJson.put("title", titulo);
                        if (SITE_ML != null && SITE_ML.equals("MLC")) {
                                productoJson.put("pictures", imagenes);
                        }
                        // productoJson.put("pictures", imagenes);
                        JSONObject shippingJson = new JSONObject();
                        shippingJson.put("mode", "me2");
                        // if (producto.getCodigoAlfa().equals("P311439_GU8")) {
                        // mercado.post("/sites/" + SITE_ML + "/shipping/selfservice/items/" + itemId ,
                        // params,"");
                        // }
                        String activarRetire = TypeTools.getCredential(credentials,
                                        Credenciales.MERCADO_ACTIVAR_RETIRO);
                        if (activarRetire == null || !activarRetire.equalsIgnoreCase("SI")) {
                                shippingJson.put("local_pick_up", false);
                        } else {
                                shippingJson.put("local_pick_up", true);
                        }

                        String gastosGratis = TypeTools.getCredential(credentials, Credenciales.MERCADO_ENVIOS_GRATIS);
                        String politicaEnvio = leerPoliticaDelModeloDeEnvio(
                                        procesoInfo.getResultadoPublicacion().getIdPublicacion(), "",
                                        ID_ATR_ENVIO_POLITICA_ENVIO, procesoInfo);
                        procesoInfo.addDescripcion("politica de envio=" + politicaEnvio);
                        if (!StringUtils.isEmpty(politicaEnvio) && politicaEnvio.trim().equals("free_shipping")) {
                                procesoInfo.addDescripcion("politica de envio=" + politicaEnvio
                                                + ", ponemos gastos e envio gratis");
                                shippingJson.put("free_shipping", true);
                        } else {
                                if (gastosGratis == null || !gastosGratis.equalsIgnoreCase("SI")) {
                                        shippingJson.put("free_shipping", false);
                                } else {
                                        shippingJson.put("free_shipping", true);
                                }
                        }

                        productoJson.put("shipping", shippingJson);
                        String categoriaS = "";
                        CategoriaCanalBD categoria = categoryService.obtenerCategoriaMapeada(
                                        producto.getIdCategorias(),
                                        PublicConstants.CANAL_MERCADO_LIBRE_ID,
                                        cmCanalSite.getIdSiteRotulo());
                        if (categoria != null && categoria.getIdCategoriaEnCanal() != null) {
                                productoJson.put("category_id", categoria.getIdCategoriaEnCanal());
                                categoriaS = categoria.getIdCategoriaEnCanal();
                        }

                        List<CategoriaCanalAtributoBD> attributes = getAttributes(mercado,
                                        categoria.getIdCategoriaCanal(),
                                        categoria.getIdCategoriaEnCanal(),
                                        procesoInfo);

                        String categoriasSinTalla = TypeTools.getCredential(credentials,
                                        Credenciales.MERCADO_CATEGORIAS_SIN_TALLA);
                        boolean noEnviarVariantes = (categoriasSinTalla != null
                                        && categoriasSinTalla.contains(categoriaS));

                        JSONArray atributos = new JSONArray();
                        // if (!site.isTOTVSWinThorERP()) {
                        // if (attributes.stream().anyMatch(attribute -> attribute.is("SIZE")) &&
                        // !noEnviarVariantes) {
                        if (hasVariation(site, procesoInfo, credentials, producto)) {
                                JSONArray variations = variations(credentials, producto, resultDetalle, numFotos, false,
                                                procesoInfo, SITE_ML);
                                productoJson.put("variations", variations);
                        } else {
                                Long stock = getStockProducto(producto);
                                if (!producto.getActivo()) {
                                        stock = 0L;
                                }
                                productoJson.put("available_quantity", stock);
                                productoJson.put("price", NumberTools.to(producto.getPrecio(), SITE_ML));

                                JSONObject atributoGtin = new JSONObject();
                                atributoGtin.put("id", "GTIN");
                                atributoGtin.put("value_name", getEanProducto(producto));
                                atributos.add(atributoGtin);
                        }

                        JSONObject atributoMarca = new JSONObject();
                        atributoMarca.put("id", "BRAND");
                        atributoMarca.put("name", "Marca");
                        atributoMarca.put("value_name", producto.getMarca());
                        atributos.add(atributoMarca);
                        JSONObject atributoGenero = new JSONObject();
                        atributoGenero.put("id", "GENDER");
                        atributoGenero.put("name", "Género");
                        String moneda = TypeTools.getCredential(credentials, Credenciales.MERCADO_CURRENCY);
                        if (moneda != null && moneda.equalsIgnoreCase("BRL")) {
                                atributoGenero.put("value_name",
                                                MercadoLibreConstants.mapaGenerosBR.get(producto.getGenero()));
                        } else {
                                atributoGenero.put("value_name",
                                                MercadoLibreConstants.mapaGeneros.get(producto.getGenero()));
                        }

                        atributos.add(atributoGenero);
                        JSONObject atributoModelo = new JSONObject();
                        atributoModelo.put("id", "MODEL");
                        atributoModelo.put("name", "Modelo");
                        atributoModelo.put("value_name", producto.getValorAtributoOrDefault("MODELO", ""));
                        atributos.add(atributoModelo);

                        String composicion = producto.getValorAtributoOrDefault("COMPOSICION", "");
                        if (!StringUtils.isEmpty(composicion)) {
                                JSONObject atributo = new JSONObject();
                                atributo.put("id", "COMPOSITION");
                                atributo.put("name", "Composition");
                                atributo.put("value_name", StringTools.limita(composicion, MAXIMUM_COMPOSICION_SIZE));
                                atributos.add(atributo);
                        }
                        if (!StringUtils.isEmpty(producto.getValorAtributoOrDefault("MANUFACTURER", ""))) {
                                JSONObject atributoFabricante = new JSONObject();
                                atributoFabricante.put("id", "MANUFACTURER");
                                atributoFabricante.put("name", "Fabricante");
                                atributoFabricante.put("value_name",
                                                producto.getValorAtributoOrDefault("MANUFACTURER", ""));
                                atributos.add(atributoFabricante);
                        }
                        String material = producto.getMaterial();
                        if (!StringUtils.isEmpty(material)) {
                                JSONObject atributo = new JSONObject();
                                atributo.put("id", "MAIN_MATERIAL");
                                atributo.put("name", "Material");
                                atributo.put("value_name", producto.getMaterial());
                                atributos.add(atributo);
                        }

                        productoJson.put("attributes", atributos);

                        String idOficinal = env.getProperty(
                                        "mercado.libre.official.store." + producto.getIdMarca() + "." + idSite);
                        if (StringUtils.isEmpty(idOficinal)) {
                                idOficinal = TypeTools.getCredential(credentials, Credenciales.MERCADO_OFICIAL_STORE);
                        }
                        if (site.getAgregacionRotulos()) {

                                String idOficinalAgrupado = env.getProperty("mercado.libre.official.store.agregador."
                                                + producto.getIdSite() + "." + idSite);
                                if (!StringUtils.isEmpty(idOficinalAgrupado)) {
                                        idOficinal = idOficinalAgrupado;
                                }
                        }
                        if (!StringUtils.isEmpty(idOficinal)) {
                                productoJson.put("official_store_id", idOficinal);
                        }

                        params.add("access_token", mercado.getAccessToken());

                        // Response r;
                        procesoInfo.addDescripcion(productoJson.toString());
                        String id = "";
                        // r = mercado.put("/items/" + itemId, params, productoJson.toString());

                        String[] headerQueryParams = { "Content-Type", "Authorization", "access_token" };
                        String[] headerQueryParamsValues = { "application/json; charset=UTF-8",
                                        "Bearer " + mercado.getAccessToken(), mercado.getAccessToken() };
                        ResponseEntity<String> res = null;
                        String respuesta = "";
                        try {
                                res = HttpClientRestTools.callRestPut("https://api.mercadolibre.com/items/" + itemId,
                                                String.class,
                                                null,
                                                null,
                                                headerQueryParams,
                                                headerQueryParamsValues, productoJson.toString(), procesoInfo);
                                respuesta = res.getBody();
                        } catch (Exception e) {
                                respuesta = e.toString();
                        }
                        procesoInfo.addDescripcion("ML items response=" + respuesta);
                        try {
                                String error = "";
                                JSONObject jObject = JSONObject.fromObject(respuesta); // json
                                String seller_id = null;
                                String status = null;
                                String message = null;
                                String errorMsg = null;

                                try {
                                        seller_id = jObject.getString("id");
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                }
                                try {
                                        status = jObject.getString("status");
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                }
                                try {
                                        message = jObject.getString("message");
                                } catch (Exception e) {
                                }
                                try {
                                        errorMsg = jObject.getString("error");
                                } catch (Exception e) {
                                }

                                procesoInfo.addDescripcion("seller_id=" + seller_id);
                                procesoInfo.addDescripcion("status=" + status);
                                procesoInfo.addDescripcion("message=" + message);
                                procesoInfo.addDescripcion("error=" + errorMsg);

                                if (/* (status != null && !status.equals("active")) || */ (StringUtils
                                                .isEmpty(seller_id))) {
                                        // vamos a enviarlo sin titulo ni categoria
                                        id = "";

                                        productoJson.remove("category_id");
                                        procesoInfo.addDescripcion("ML items REQUEST DESPUES DE PRIMER ERROR="
                                                        + productoJson.toString());
                                        // r = mercado.put("/items/" + itemId, params, productoJson.toString());

                                        res = null;
                                        respuesta = "";
                                        try {
                                                res = HttpClientRestTools.callRestPut(
                                                                "https://api.mercadolibre.com/items/" + itemId,
                                                                String.class,
                                                                null,
                                                                null,
                                                                headerQueryParams,
                                                                headerQueryParamsValues, productoJson.toString(),
                                                                procesoInfo);
                                                respuesta = res.getBody();
                                        } catch (Exception e) {
                                                respuesta = e.toString();
                                        }

                                        procesoInfo.addDescripcion("ML items response=" + respuesta);
                                        jObject = JSONObject.fromObject(respuesta); // json
                                        seller_id = null;
                                        status = null;
                                        message = null;
                                        errorMsg = null;

                                        try {
                                                seller_id = jObject.getString("id");
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("", e);
                                        }
                                        try {
                                                status = jObject.getString("status");
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("", e);
                                        }
                                        try {
                                                message = jObject.getString("message");
                                        } catch (Exception e) {
                                        }
                                        try {
                                                errorMsg = jObject.getString("error");
                                        } catch (Exception e) {
                                        }

                                        procesoInfo.addDescripcion("seller_id=" + seller_id);
                                        procesoInfo.addDescripcion("status=" + status);
                                        procesoInfo.addDescripcion("message=" + message);
                                        procesoInfo.addDescripcion("error=" + errorMsg);
                                        if (/* (status != null && !status.equals("active")) || */ (StringUtils
                                                        .isEmpty(seller_id))) {
                                                // vamos a enviarlo sin titulo ni categoria
                                                id = "";
                                                productoJson.remove("title");
                                                productoJson.remove("category_id");
                                                procesoInfo.addDescripcion("ML items REQUEST DESPUES DE SEFUNDO ERROR="
                                                                + productoJson.toString());
                                                // r = mercado.put("/items/" + itemId, params, productoJson.toString());

                                                res = null;
                                                respuesta = "";
                                                try {
                                                        res = HttpClientRestTools.callRestPut(
                                                                        "https://api.mercadolibre.com/items/" + itemId,
                                                                        String.class,
                                                                        null,
                                                                        null,
                                                                        headerQueryParams,
                                                                        headerQueryParamsValues,
                                                                        productoJson.toString(), procesoInfo);
                                                        respuesta = res.getBody();
                                                } catch (Exception e) {
                                                        respuesta = e.toString();
                                                }

                                                procesoInfo.addDescripcion("ML items response=" + respuesta);
                                                jObject = JSONObject.fromObject(respuesta); // json
                                                seller_id = null;
                                                status = null;
                                                message = null;
                                                errorMsg = null;

                                                try {
                                                        seller_id = jObject.getString("id");
                                                } catch (Exception e) {
                                                        procesoInfo.addDescripcion("", e);
                                                }
                                                try {
                                                        status = jObject.getString("status");
                                                } catch (Exception e) {
                                                        procesoInfo.addDescripcion("", e);
                                                }
                                                try {
                                                        message = jObject.getString("message");
                                                } catch (Exception e) {
                                                }
                                                try {
                                                        errorMsg = jObject.getString("error");
                                                } catch (Exception e) {
                                                }

                                                procesoInfo.addDescripcion("seller_id=" + seller_id);
                                                procesoInfo.addDescripcion("status=" + status);
                                                procesoInfo.addDescripcion("message=" + message);
                                                procesoInfo.addDescripcion("error=" + errorMsg);
                                        }

                                        if (/* (status != null && !status.equals("active")) || */ (StringUtils
                                                        .isEmpty(seller_id))) {
                                                resultDetalle.setOk(false);
                                                resultDetalle.setError(respuesta);
                                                return resultDetalle;
                                        } else {
                                                resultDetalle.setOk(true);
                                                resultDetalle.setError(
                                                                "Updated without category and title for being in an deal");
                                        }
                                }
                                JSONArray array = jObject.getJSONArray("variations");
                                for (int i = 0; i < array.size(); i++) {
                                        JSONObject oVariante = (JSONObject) array.get(i);
                                        String seller_custom_field = oVariante.getString("seller_custom_field");
                                        procesoInfo.addDescripcion("seller_custom_field=" + seller_custom_field);
                                        String idV = oVariante.getLong("id") + "";
                                        procesoInfo.addDescripcion("idV=" + idV);
                                        List<ResultadoPublicacionProductoVariantM> variantes = resultDetalle
                                                        .getVariants();
                                        for (ResultadoPublicacionProductoVariantM varian : variantes) {
                                                if (varian.getSkuHermesChannel().equals(seller_custom_field)) {
                                                        varian.setIdItemChannel(idV);
                                                }
                                        }
                                }
                                resultDetalle.setIdItemChannel(seller_id);
                                resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
                                resultDetalle.setOk(true);
                                /*
                                 * if (data != null && data.getString("RequestId") != null) {
                                 * procesoInfo.addDescripcion(
                                 * "request was succesfull, requestid: " + data.getString("RequestId")); result
                                 * = data.getString("RequestId"); }
                                 */

                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                                resultDetalle.setError(e.getMessage());
                                resultDetalle.setOk(false);
                        }
                        // actualizamos la descripcion
                        procesoInfo.addDescripcion("enviamosdescripcion=" + "/items/" + itemId + "/description="
                                        + descripcionJson.toString());

                        Response rDescripcion = null;
                        String respuestaDes = "";
                        try {
                                res = HttpClientRestTools.callRestPut(
                                                "https://api.mercadolibre.com/items/" + itemId + "/description",
                                                String.class,
                                                null,
                                                null,
                                                headerQueryParams,
                                                headerQueryParamsValues, descripcionJson.toString(), procesoInfo);
                                respuestaDes = res.getBody();
                        } catch (Exception e) {
                                respuestaDes = e.toString();
                        }

                        procesoInfo.addDescripcion(respuestaDes);

                        String tiempoDisponibilidad = TypeTools.getCredential(credentials,
                                        Credenciales.MERCADO_TIEMPO_DISPONIBILIDAD);
                        procesoInfo.addDescripcion("tiempoDisponibilidad=" + tiempoDisponibilidad);
                        if (!StringUtils.isEmpty(tiempoDisponibilidad)) {
                                try {
                                        JSONArray listaTemr = new JSONArray();
                                        JSONObject temr = new JSONObject();
                                        temr.put("id", "MANUFACTURING_TIME");
                                        temr.put("value_name", tiempoDisponibilidad);
                                        listaTemr.add(temr);
                                        procesoInfo.addDescripcion("enviamos tiempo disponibilidad=" + "/items/"
                                                        + itemId + "=" + listaTemr.toString());
                                        Response rTern = mercado.put("/items/" + itemId, params, listaTemr.toString());
                                        procesoInfo.addDescripcion(rTern.getResponseBody());
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                        procesoInfo.addDescripcion("", e);
                                }

                        }
                        if (activarRetire != null && activarRetire.equalsIgnoreCase("SI")) {
                                String activarRetireHoras = TypeTools.getCredential(credentials,
                                                Credenciales.MERCADO_ACTIVAR_RETIRO_HORAS);
                                if (StringUtils.isEmpty(activarRetireHoras))
                                        activarRetireHoras = "72";
                                JSONObject retiro = new JSONObject();
                                retiro.put("availability_time_in_hours", Integer.parseInt(activarRetireHoras));

                                try {
                                        // eliminamos tiendas asociadas al item

                                        String respuestaTiendas = mercado.httpGet("/items/" + itemId + "/stores", null,
                                                        null, headerQueryParams, headerQueryParamsValues);
                                        procesoInfo.addDescripcion(respuestaTiendas);

                                        /*
                                         * Response rStores=mercado.get("/items/"+itemId+"/stores");
                                         * procesoInfo.addDescripcion(rStores.getResponseBody());
                                         * JSONObject jObject = JSONObject.fromObject(respuesta); //
                                         */

                                        List<String> idTiendas = new ArrayList<>();
                                        try {
                                                JSONObject jObject = JSONObject.fromObject(respuestaTiendas); // json*/

                                                JSONArray tiendas = jObject.getJSONArray("results");
                                                for (int j = 0; j < tiendas.size(); j++) {
                                                        JSONObject tienda = (JSONObject) tiendas.get(j);
                                                        idTiendas.add(tienda.getString("id"));
                                                }
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("", e);
                                                resultDetalle.setError(e.getMessage());
                                        }

                                        List<String> tiendasConStock = new ArrayList<>();
                                        try {
                                                for (SubProductoDto sub : producto.getSubProductos()) {
                                                        for (StockDto stock : sub.getStocks()) {
                                                                if (stock.getStock() > 0) {
                                                                        AlmacenDto almacen = almacenes.stream()
                                                                                        .filter(x -> stock
                                                                                                        .getIdAlmacen()
                                                                                                        .equals(x.getIdAlmacen()))
                                                                                        .findAny()
                                                                                        .orElse(null);
                                                                        String idML = (String) almacen.getAtributo(
                                                                                        PublicConstants.ML_ID_ALMACEN
                                                                                                        + "_"
                                                                                                        + site.getIdSite());
                                                                        procesoInfo.addDescripcion("Atributo ML(" + idML
                                                                                        + ")" + " para el almacen="
                                                                                        + almacen.getIdPmm());
                                                                        if (almacen != null
                                                                                        && !StringUtils.isEmpty(idML)) {
                                                                                tiendasConStock.add(idML);
                                                                        }
                                                                }
                                                        }
                                                }
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("", e);
                                                resultDetalle.setError(e.getMessage());
                                        }

                                        String[] queryParams = { "access_token" };
                                        String[] queryParamsValues = { mercado.getAccessToken() };

                                        // vamos a ver que tiendas debemos eliminar/crear
                                        for (String tiendaConStock : tiendasConStock) {
                                                if (!idTiendas.contains(tiendaConStock)) {
                                                        procesoInfo.addDescripcion("vamos a anuadir la tienda="
                                                                        + tiendaConStock + " al producto");
                                                        // Response
                                                        // rStore=mercado.post("/items/"+itemId+"/stores/"+tiendaConStock,params,retiro.toString());

                                                        ResponseEntity<String> rStore = mercado.httpPost(
                                                                        "/items/" + itemId + "/stores/"
                                                                                        + tiendaConStock,
                                                                        queryParams, queryParamsValues,
                                                                        headerQueryParams, headerQueryParamsValues,
                                                                        procesoInfo, retiro.toString());
                                                        procesoInfo.addDescripcion(rStore.getBody());
                                                }
                                                idTiendas.remove(tiendaConStock);
                                        }

                                        // recorremos las que quedan para eliminarlas
                                        for (String tiendaML : idTiendas) {
                                                procesoInfo.addDescripcion("vamos a elimnar la tienda=" + tiendaML
                                                                + " al producto");
                                                Response rStore = mercado.delete(
                                                                "/items/" + itemId + "/stores/" + tiendaML, params);
                                                procesoInfo.addDescripcion(rStore.getResponseBody());
                                        }
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                        resultDetalle.setError(e.getMessage());
                                }
                        }
                        Boolean enviarGastosGratis = false;
                        if (!StringUtils.isEmpty(politicaEnvio) && politicaEnvio.trim().equals("free_shipping")) {
                                enviarGastosGratis = true;
                        } else {
                                if (gastosGratis == null || !gastosGratis.equalsIgnoreCase("SI")) {
                                        enviarGastosGratis = false;
                                } else {
                                        enviarGastosGratis = true;
                                }
                        }
                        if (enviarGastosGratis) {
                                res = null;
                                respuesta = "";
                                try {

                                        procesoInfo.addDescripcion("Enviamos gastos envio gratis a"
                                                        + "https://api.mercadolibre.com/items/" + itemId
                                                        + "/shipping_options/free");
                                        res = HttpClientRestTools.callRestGet(
                                                        "https://api.mercadolibre.com/items/" + itemId
                                                                        + "/shipping_options/free",
                                                        String.class,
                                                        null,
                                                        null,
                                                        null,
                                                        null);
                                        respuesta = res.getBody();
                                        procesoInfo.addDescripcion("respuesta envio gratis=" + respuesta);
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                        respuesta = e.toString();
                                }
                        }

                } catch (Exception e) {
                        procesoInfo.addDescripcion("", e);
                        resultDetalle.setError(e.getMessage());
                        resultDetalle.setOk(false);
                }
                return resultDetalle;
        }

        public ResultadoPublicacionProductoM updateItemsInternalOnlyCategoria(CanalSiteBD cmCanalSite,
                        ProductoCollectionDto producto, String idSite, SiteDto site,
                        Boolean precioLleno, String itemId, Meli mercado, String garantia,
                        List<CredentialsSiteChannelDto> credentials, ResultadoPublicacionProductoM resultDetalle,
                        @SuppressWarnings("rawtypes") ProcesoInfo procesoInfo, List<AlmacenDto> almacenes)
                        throws Exception {

                String result = "";
                String SITE_ML = TypeTools.getCredential(credentials, Credenciales.MERCADO_MELI_COUNTRY_CODE);
                try {

                        procesoInfo.addDescripcion("item=" + itemId);
                        FluentStringsMap params = new FluentStringsMap();
                        JSONObject productoJson = new JSONObject();
                        String categoriaS = "";
                        CategoriaCanalBD categoria = categoryService.obtenerCategoriaMapeada(
                                        producto.getIdCategorias(),
                                        PublicConstants.CANAL_MERCADO_LIBRE_ID,
                                        cmCanalSite.getIdSiteRotulo());
                        if (categoria != null && categoria.getIdCategoriaEnCanal() != null) {
                                productoJson.put("category_id", categoria.getIdCategoriaEnCanal());
                                categoriaS = categoria.getIdCategoriaEnCanal();
                        } else {
                                return resultDetalle;
                        }

                        params.add("access_token", mercado.getAccessToken());

                        // Response r;
                        procesoInfo.addDescripcion(productoJson.toString());
                        String id = "";
                        // r = mercado.put("/items/" + itemId, params, productoJson.toString());

                        String[] headerQueryParams = { "Content-Type", "Authorization", "access_token" };
                        String[] headerQueryParamsValues = { "application/json; charset=UTF-8",
                                        "Bearer " + mercado.getAccessToken(), mercado.getAccessToken() };
                        ResponseEntity<String> res = null;
                        String respuesta = "";
                        try {
                                res = HttpClientRestTools.callRestPut("https://api.mercadolibre.com/items/" + itemId,
                                                String.class,
                                                null,
                                                null,
                                                headerQueryParams,
                                                headerQueryParamsValues, productoJson.toString(), procesoInfo);
                                respuesta = res.getBody();
                        } catch (Exception e) {
                                respuesta = e.toString();
                        }
                        procesoInfo.addDescripcion("ML items response=" + respuesta);

                        String error = "";
                        JSONObject jObject = JSONObject.fromObject(respuesta); // json
                        String seller_id = null;
                        String status = null;
                        String message = null;
                        String errorMsg = null;

                        try {
                                seller_id = jObject.getString("id");
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                        }
                        try {
                                status = jObject.getString("status");
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                        }
                        try {
                                message = jObject.getString("message");
                        } catch (Exception e) {
                        }
                        try {
                                errorMsg = jObject.getString("error");
                        } catch (Exception e) {
                        }

                        procesoInfo.addDescripcion("seller_id=" + seller_id);
                        procesoInfo.addDescripcion("status=" + status);
                        procesoInfo.addDescripcion("message=" + message);
                        procesoInfo.addDescripcion("error=" + errorMsg);

                        res = HttpClientRestTools.callRestPut("https://api.mercadolibre.com/items/" + itemId,
                                        String.class,
                                        null,
                                        null,
                                        headerQueryParams,
                                        headerQueryParamsValues, productoJson.toString(), procesoInfo);
                        respuesta = res.getBody();

                        procesoInfo.addDescripcion("ML items response=" + respuesta);
                        jObject = JSONObject.fromObject(respuesta); // json
                        seller_id = null;
                        status = null;
                        message = null;
                        errorMsg = null;

                        try {
                                seller_id = jObject.getString("id");
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                        }
                        try {
                                status = jObject.getString("status");
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                        }
                        try {
                                message = jObject.getString("message");
                        } catch (Exception e) {
                        }
                        try {
                                errorMsg = jObject.getString("error");
                        } catch (Exception e) {
                        }

                        procesoInfo.addDescripcion("seller_id=" + seller_id);
                        procesoInfo.addDescripcion("status=" + status);
                        procesoInfo.addDescripcion("message=" + message);
                        procesoInfo.addDescripcion("error=" + errorMsg);

                        if (/* (status != null && !status.equals("active")) || */ (StringUtils.isEmpty(seller_id))) {
                                resultDetalle.setOk(false);
                                resultDetalle.setError(respuesta);
                                return resultDetalle;
                        } else {
                                resultDetalle.setOk(true);
                                resultDetalle.setError("Updated without category and title for being in an deal");
                        }

                } catch (Exception e) {
                        procesoInfo.addDescripcion("", e);
                        resultDetalle.setError(e.getMessage());
                        resultDetalle.setOk(false);
                }
                return resultDetalle;
        }

        public ResultadoPublicacionProductoM updatePricesInternal(CanalSiteBD cmCanalSite,
                        ProductoCollectionDto producto, SiteDto site,
                        String itemId, Meli mercado, String idOferta, List<CredentialsSiteChannelDto> credentials,
                        ResultadoPublicacionProductoM resultDetalle,
                        @SuppressWarnings("rawtypes") ProcesoInfo procesoInfo, Boolean enOferta, String country)
                        throws Exception {

                String result = "";
                FluentStringsMap params = new FluentStringsMap();
                params.add("access_token", mercado.getAccessToken());
                Response r;
                procesoInfo.addDescripcion("item=" + itemId);
                procesoInfo.addDescripcion("PrecioRebajado=" + producto.getPrecioRebajado());
                procesoInfo.addDescripcion("Precio=" + producto.getPrecio());
                String sellerId = TypeTools.getCredential(credentials, Credenciales.MERCADO_LIBRE_ORDERID);
                try {

                        // intengtamos luego actualizar oferta
                        if (producto.getPrecioRebajado() < producto.getPrecio() && !StringUtils.isEmpty(idOferta)) {

                                enOferta = false;

                                JSONObject productoJson = new JSONObject();
                                String urlPath = "";

                                String seller_id = null;
                                String status = null;
                                String message = null;
                                String errorMsg = null;
                                String respuesta = "";
                                // hemos detectado problemas, asi que vamos a intentar borrar siempre el
                                // producto
                                procesoInfo.addDescripcion("item=" + itemId + "- inicio delete deal");
                                try {
                                        // if (enOferta) {
                                        procesoInfo.addDescripcion("delete:/seller-promotions/items/" + itemId
                                                        + "?promotion_type=DEAL&deal_id=" + idOferta);
                                        r = mercado.delete(
                                                        "/seller-promotions/items/" + itemId
                                                                        + "?promotion_type=DEAL&deal_id=" + idOferta,
                                                        params);
                                        procesoInfo.addDescripcion(r.getResponseBody());
                                        // }
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion(e.toString());
                                }
                                procesoInfo.addDescripcion("item=" + itemId + "- fin delete deal");
                                procesoInfo.addDescripcion("item=" + itemId + "- inicio create deal");

                                // if (!enOferta) {
                                // procesoInfo.addDescripcion("item=" + itemId +" NO estaba en una oferta,
                                // hacemos post");
                                productoJson.put("deal_id", idOferta);
                                productoJson.put("deal_price", NumberTools.to(producto.getPrecioRebajado(), country));
                                productoJson.put("regular_price", NumberTools.to(producto.getPrecio(), country));
                                productoJson.put("promotion_type", "DEAL");

                                procesoInfo.addDescripcion("POST=" + productoJson.toString());

                                urlPath = "/seller-promotions/items/" + itemId;
                                urlPath = urlPath.replace("{User_id}", sellerId);
                                urlPath = urlPath.replace("{Deal_id}", idOferta);

                                procesoInfo.addDescripcion(urlPath);

                                String[] queryParams = { "access_token" };
                                String[] queryParamsValues = { mercado.getAccessToken() };

                                String[] headerQueryParams = { "Accept" };
                                String[] headerQueryParamsValues = { "application/json" };

                                ResponseEntity<String> response = mercado.httpPost(urlPath, queryParams,
                                                queryParamsValues, headerQueryParams, headerQueryParamsValues,
                                                procesoInfo, productoJson.toString());
                                // r = mercado.post(urlPath, params, productoJson.toString());

                                Double priceML = null;
                                Double priceRebajadoML = null;

                                try {
                                        procesoInfo.addDescripcion("response=" + response.getBody());
                                        JSONObject jObject = JSONObject.fromObject(response.getBody()); // json
                                        seller_id = null;

                                        try {
                                                priceML = jObject.getDouble("price");
                                                priceRebajadoML = jObject.getDouble("original_price");
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("", e);
                                        }
                                        if (priceML == null) {
                                                enOferta = true;
                                        }
                                        try {
                                                status = jObject.getString("status");
                                                if (status.equals("rejected")) {
                                                        enOferta = true;
                                                }
                                        } catch (Exception e) {
                                                // procesoInfo.addDescripcion("", e);
                                        }
                                        try {
                                                message = jObject.getString("message");
                                        } catch (Exception e) {
                                        }
                                        try {
                                                errorMsg = jObject.getString("error");
                                        } catch (Exception e) {
                                        }
                                        respuesta = response.getBody();
                                        procesoInfo.addDescripcion("POST seller_id=" + seller_id);
                                        procesoInfo.addDescripcion("POST status=" + status);
                                        procesoInfo.addDescripcion("POST message=" + message);
                                        procesoInfo.addDescripcion("POST error=" + errorMsg);

                                        procesoInfo.addDescripcion("POST access_token=" + mercado.getAccessToken());
                                        procesoInfo.addDescripcion("POST " + urlPath);
                                        procesoInfo.addDescripcion("POST REQUEST=" + productoJson.toString());
                                        procesoInfo.addDescripcion("POST RESPONSE=" + response.getBody());
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("", e);
                                        enOferta = true;
                                }
                                // }
                                procesoInfo.addDescripcion("item=" + itemId + "- fin create deal");

                                if (enOferta) {
                                        procesoInfo.addDescripcion("item=" + itemId
                                                        + " SI estaba en una oferta o ha dado fallo al insertar, hacemos put");
                                        productoJson.put("deal_id", idOferta);
                                        productoJson.put("deal_price",
                                                        NumberTools.to(producto.getPrecioRebajado(), country));
                                        productoJson.put("regular_price",
                                                        NumberTools.to(producto.getPrecio(), country));
                                        productoJson.put("promotion_type", "DEAL");

                                        procesoInfo.addDescripcion("put=" + productoJson.toString());

                                        urlPath = "/seller-promotions/items/" + itemId;
                                        procesoInfo.addDescripcion(urlPath);
                                        r = mercado.put(urlPath, params, productoJson.toString());

                                        procesoInfo.addDescripcion("response=" + r.getResponseBody());

                                        String error = "";
                                        JSONObject jObject = JSONObject.fromObject(r.getResponseBody()); // json

                                        try {
                                                priceML = jObject.getDouble("price");
                                                priceRebajadoML = jObject.getDouble("original_price");
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("", e);
                                        }

                                        try {
                                                seller_id = jObject.getString("item_id");
                                        } catch (Exception e) {
                                                // procesoInfo.addDescripcion("", e);
                                        }
                                        try {
                                                status = jObject.getString("status");
                                        } catch (Exception e) {
                                                // procesoInfo.addDescripcion("", e);
                                        }
                                        try {
                                                message = jObject.getString("message");
                                        } catch (Exception e) {
                                        }
                                        try {
                                                errorMsg = jObject.getString("error");
                                        } catch (Exception e) {
                                        }
                                        respuesta = r.getResponseBody();
                                        procesoInfo.addDescripcion("PUT seller_id=" + seller_id);
                                        procesoInfo.addDescripcion("PUT status=" + status);
                                        procesoInfo.addDescripcion("PUT message=" + message);
                                        procesoInfo.addDescripcion("PUT error=" + errorMsg);

                                        procesoInfo.addDescripcion("PUT access_token=" + mercado.getAccessToken());
                                        procesoInfo.addDescripcion("PUT " + urlPath);
                                        procesoInfo.addDescripcion("PUT REQUEST=" + productoJson.toString());
                                        procesoInfo.addDescripcion("PUT RESPONSE=" + r.getResponseBody());

                                }

                                if (priceML == null || priceRebajadoML == null) {
                                        resultDetalle.setOk(false);
                                        resultDetalle.setError(respuesta);
                                        return resultDetalle;
                                }

                                resultDetalle.setIdItemChannel(seller_id);
                                resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
                                resultDetalle.setOk(true);
                                resultDetalle.setAlphaCode(producto.getCodigoAlfa());
                                resultDetalle.setPrice(producto.getPrecioRebajado() + "");
                                resultDetalle.setPriceDiscount(producto.getPrecioRebajado() + "");

                        } else {
                                try {
                                        // if (enOferta) {
                                        // if (enOferta) {
                                        procesoInfo.addDescripcion("delete:/seller-promotions/items/" + itemId
                                                        + "?promotion_type=DEAL&deal_id=" + idOferta);
                                        r = mercado.delete(
                                                        "/seller-promotions/items/" + itemId
                                                                        + "?promotion_type=DEAL&deal_id=" + idOferta,
                                                        params);
                                        procesoInfo.addDescripcion("response=" + r.getResponseBody());
                                        // }
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion(e.toString());
                                }

                                List<CategoriaCanalAtributoBD> attributes = getAttributes(mercado,
                                                cmCanalSite.getIdSiteRotulo(),
                                                producto.getIdCategorias(), procesoInfo);

                                resultDetalle = updateVariantesInternal(producto, attributes, site,
                                                itemId, mercado, procesoInfo, resultDetalle, true, country,
                                                credentials);
                                resultDetalle.setPrice(producto.getPrecioRebajado() + "");
                                resultDetalle.setAlphaCode(producto.getCodigoAlfa());
                                resultDetalle.setPriceDiscount(producto.getPrecioRebajado() + "");
                        }
                } catch (Exception e) {
                        procesoInfo.addDescripcion("", e);
                        resultDetalle.setError(e.getMessage());
                        resultDetalle.setOk(false);
                }
                // }
                return resultDetalle;
        }

        private JSONArray variationStock(ProductoCollectionDto producto, ResultadoPublicacionProductoM resultDetail,
                        ProcesoInfo procesoInfo, Boolean price, String pais) {
                JSONArray variations = new JSONArray();

                List<ResultadoPublicacionProductoVariantM> resultsSub = new ArrayList<>();
                Hashtable<String, SubProductoDto> hash = new Hashtable<>();

                for (SubProductoDto prSubProducto : producto.getSubProductos()) {
                        SubProductoDto sub = hash.get(prSubProducto.getValor());
                        if (sub != null) {
                                if (sub.getStockEnSite() <= 0) {
                                        hash.put(prSubProducto.getValor(), prSubProducto);
                                }
                        } else {
                                hash.put(prSubProducto.getValor(), prSubProducto);
                        }
                }
                Enumeration enumeration = hash.elements();
                while (enumeration.hasMoreElements()) {
                        SubProductoDto subProducto = (SubProductoDto) enumeration.nextElement();
                        Long stock = subProducto.getStockEnSite();
                        try {
                                if (stock == null || stock < 0) {
                                        stock = 0L;
                                }
                                if (!producto.getActivo()) {
                                        stock = 0L;
                                }
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("########O######### Error: ", e);
                                if (stock == null || stock < 0) {
                                        stock = 0L;
                                }
                        }
                        JSONObject productoVariationJson = new JSONObject();
                        productoVariationJson.put("available_quantity", stock);

                        if (price) {
                                productoVariationJson.put("price", NumberTools.to(producto.getPrecio(), pais));
                        }
                        ResultadoPublicacionProductoVariantM resultSub = new ResultadoPublicacionProductoVariantM();
                        String atr = (String) subProducto.getAtributoStr("ID_ITEM_ML");
                        if (!StringUtils.isEmpty(atr)) {
                                productoVariationJson.put("id", atr);
                        } else {
                                resultSub.setOk(false);
                                resultSub.setError("SKU without ID_ITEM_ML");
                                resultsSub.add(resultSub);
                                continue;
                        }

                        variations.add(productoVariationJson);

                        resultSub.setStock(subProducto.getStockEnSite() + "");
                        resultSub.setEan(subProducto.getEan());
                        try {
                                resultSub.setSize(subProducto.getTallajeOriginalCliente());
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                        }
                        resultSub.setSkuHermesChannel(subProducto.getIdArticuloModalia());
                        resultSub.setIdSubProducto(subProducto.getIdSubProducto());
                        resultsSub.add(resultSub);
                }
                resultDetail.setVariants(resultsSub);
                return variations;
        }

        public ResultadoPublicacionProductoM updateVariantesInternal(ProductoCollectionDto producto,
                        List<CategoriaCanalAtributoBD> attributes,
                        SiteDto site, String itemId, Meli mercado, ProcesoInfo procesoInfo,
                        ResultadoPublicacionProductoM resultDetail, Boolean price, String pais,
                        List<CredentialsSiteChannelDto> credentials) {
                String result = "";

                try {
                        procesoInfo.addDescripcion("item=" + itemId);
                        FluentStringsMap params = new FluentStringsMap();
                        JSONObject productoJson = new JSONObject();
                        Boolean tieneId = false;
                        Long stock = 0L;
                        for (SubProductoDto subProducto : producto.getSubProductos()) {
                                stock = subProducto.getStockEnSite();
                                try {
                                        if (stock == null || stock < 0) {
                                                stock = 0L;
                                        }
                                        if (!producto.getActivo()) {
                                                stock = 0L;
                                        }
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("########O######### Error: ", e);
                                        if (stock == null || stock < 0) {
                                                stock = 0L;
                                        }
                                }
                                String atr = subProducto.getAtributoStr("ID_ITEM_ML");
                                if (!StringUtils.isEmpty(atr)) {
                                        tieneId = true;
                                }
                        }

                        // if (tieneId &&!site.isTOTVSWinThorERP()) {
                        if (tieneId && hasVariation(site, procesoInfo, credentials, producto)) {
                                JSONArray variations = variationStock(producto, resultDetail, procesoInfo, price, pais);
                                productoJson.put("variations", variations);
                        } else {
                                productoJson.put("available_quantity", stock + "");
                        }

                        params.add("access_token", mercado.getAccessToken());

                        // Response r;
                        procesoInfo.addDescripcion(productoJson.toString());
                        String id = "";

                        String[] headerQueryParams = { "Content-Type", "Authorization", "access_token" };
                        String[] headerQueryParamsValues = { "application/json; charset=UTF-8",
                                        "Bearer " + mercado.getAccessToken(), mercado.getAccessToken() };

                        ResponseEntity<String> res = null;
                        String respuesta = "";
                        try {
                                res = HttpClientRestTools.callRestPut("https://api.mercadolibre.com/items/" + itemId,
                                                String.class,
                                                null,
                                                null,
                                                headerQueryParams,
                                                headerQueryParamsValues, productoJson.toString(), procesoInfo);
                                respuesta = res.getBody();
                        } catch (Exception e) {
                                respuesta = e.toString();
                        }

                        procesoInfo.addDescripcion(respuesta);

                        String error = "";
                        JSONObject jObject = JSONObject.fromObject(respuesta); // json
                        String seller_id = null;
                        String status = null;
                        String message = null;
                        String errorMsg = null;

                        try {
                                seller_id = jObject.getString("id");
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                        }
                        try {
                                status = jObject.getString("status");
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                        }
                        try {
                                message = jObject.getString("message");
                        } catch (Exception e) {
                        }
                        try {
                                errorMsg = jObject.getString("error");

                        } catch (Exception e) {
                        }
                        if (status == null)
                                status = "";
                        if (message != null && (message.equals("expired_token") || message.equals("invalid_token")
                                        || message.equalsIgnoreCase("Invalid token")
                                        || status.equalsIgnoreCase("403"))) {
                                hashMelis.remove(site.getIdSite());
                                resultDetail.setOk(false);
                                resultDetail.setError(message);
                                return resultDetail;
                        }

                        procesoInfo.addDescripcion("seller_id=" + seller_id);
                        procesoInfo.addDescripcion("status=" + status);
                        procesoInfo.addDescripcion("message=" + message);
                        procesoInfo.addDescripcion("error=" + errorMsg);

                        if (StringUtils.isEmpty(seller_id)) {
                                resultDetail.setOk(false);
                                resultDetail.setError(respuesta);
                                return resultDetail;
                        }
                        JSONArray array = jObject.getJSONArray("variations");
                        for (int i = 0; i < array.size(); i++) {
                                JSONObject oVariante = (JSONObject) array.get(i);
                                procesoInfo.addDescripcion("variante=" + oVariante.toString());
                                String seller_custom_field = oVariante.getString("seller_custom_field");
                                procesoInfo.addDescripcion("seller_custom_field=" + seller_custom_field);
                                String idV = null;
                                try {
                                        idV = oVariante.getString("id");
                                } catch (Exception e) {
                                        Long idL = oVariante.getLong("id");
                                        idV = idL + "";
                                }
                                procesoInfo.addDescripcion("idV=" + idV);
                                List<ResultadoPublicacionProductoVariantM> variantes = resultDetail.getVariants();
                                for (ResultadoPublicacionProductoVariantM varian : variantes) {
                                        if (varian != null && varian.getSkuHermesChannel() != null
                                                        && varian.getSkuHermesChannel().equals(seller_custom_field)) {
                                                varian.setIdItemChannel(idV);
                                        }
                                }
                        }
                        resultDetail.setIdItemChannel(seller_id);
                        resultDetail.setSkuHermesChannel(producto.getIdArticuloModalia());
                        resultDetail.setOk(true);
                } catch (Exception e) {
                        procesoInfo.addDescripcion("", e);
                        procesoInfo.addDescripcion("", e);
                        resultDetail.setError(e.getMessage());
                        resultDetail.setOk(false);
                }
                return resultDetail;
        }

        public ResultadoPublicacionProductoM getProduct(List<CredentialsSiteChannelDto> credentials,
                        String idProductoCanal,
                        String idSite, Meli mercado, ProcesoInfo procesoInfo) throws MeliException {
                String sellerId = TypeTools.getCredential(credentials, "mercado.libre.clientId.order");
                String urlPath = "/items/{item}?attributes=variations";
                urlPath = urlPath.replace("{item}", idProductoCanal);
                ResultadoPublicacionProductoM resultDetail = new ResultadoPublicacionProductoM();
                // Response r = mercado.get(urlPath);
                ResponseEntity<String> res = null;
                String respuesta = "";
                try {
                        res = HttpClientRestTools.callRestGet(
                                        "https://api.mercadolibre.com/items/" + idProductoCanal
                                                        + "?attributes=variations",
                                        String.class,
                                        null,
                                        null,
                                        null,
                                        null);
                        respuesta = res.getBody();
                } catch (Exception e) {
                        respuesta = e.toString();
                }

                try {
                        procesoInfo.addDescripcion(respuesta);
                        JSONObject jObject = JSONObject.fromObject(respuesta); // json
                        JSONArray array = jObject.getJSONArray("variations");
                        List<ResultadoPublicacionProductoVariantM> resultsSub = new ArrayList<ResultadoPublicacionProductoVariantM>();
                        for (int i = 0; i < array.size(); i++) {
                                JSONObject oVariante = (JSONObject) array.get(i);
                                // String seller_custom_field= oVariante.getString("seller_custom_field");
                                // procesoInfo.addDescripcion("seller_custom_field="+seller_custom_field);
                                String idV = null;
                                try {
                                        idV = oVariante.getString("id");
                                } catch (Exception e) {
                                        Long idL = oVariante.getLong("id");
                                        idV = idL + "";
                                }

                                procesoInfo.addDescripcion("idV=" + idV);
                                JSONArray arrayCombinations = oVariante.getJSONArray("attribute_combinations");
                                String seller_custom_field = "";
                                for (int j = 0; j < arrayCombinations.size(); j++) {
                                        JSONObject oCombination = (JSONObject) arrayCombinations.get(j);
                                        String idC = oCombination.getString("id");
                                        procesoInfo.addDescripcion("idC=" + idC);
                                        if (idC.equalsIgnoreCase("SIZE")) {
                                                String value_name = oCombination.getString("value_name");
                                                procesoInfo.addDescripcion("value_name=" + value_name);
                                                seller_custom_field = value_name;
                                        }
                                }
                                ResultadoPublicacionProductoVariantM variante = new ResultadoPublicacionProductoVariantM();
                                ;
                                variante.setIdItemChannel(idV);
                                variante.setSkuHermesChannel(seller_custom_field);
                                resultsSub.add(variante);
                        }
                        resultDetail.setVariants(resultsSub);
                } catch (Exception e) {
                        procesoInfo.addDescripcion("", e);
                }
                return resultDetail;
        }

        @Override
        public List<ResultadoPublicacionProductoM> getProductosCanal(SiteDto site,
                        List<CredentialsSiteChannelDto> credentials,
                        ProcesoInfo procesoInfo, CanalSiteBD cmCanalSite) throws NumberFormatException, Exception {
                return getProducts(credentials, cmCanalSite.getIdSiteCanal() + "", null, procesoInfo, "");
        }

        public List<ResultadoPublicacionProductoM> getProducts(List<CredentialsSiteChannelDto> credentials,
                        String idSite, Meli mercado, ProcesoInfo procesoInfo, String status)
                        throws NumberFormatException, Exception {
                List<ResultadoPublicacionProductoM> resultDetail = new ArrayList<ResultadoPublicacionProductoM>();
                // Response r = mercado.get(urlPath);
                ResponseEntity<String> res = null;
                String SITE_ML = TypeTools.getCredential(credentials, Credenciales.MERCADO_MELI_COUNTRY_CODE);

                if (mercado == null) {
                        mercado = getMeli(credentials, false, Long.parseLong(idSite), procesoInfo);
                }

                Integer offset = 0;
                Boolean continuar = true;
                ;
                while (continuar) {
                        try {
                                String respuesta = "";

                                procesoInfo.addDescripcion(
                                                "https://api.mercadolibre.com/sites/" + SITE_ML
                                                                + "/search?include_filters=true&offset=" + offset
                                                                + "&search_type=scan&seller_id="
                                                                + TypeTools.getCredential(credentials,
                                                                                Credenciales.MERCADO_LIBRE_ORDERID)
                                                                + status);
                                res = HttpClientRestTools.callRestGet("https://api.mercadolibre.com/sites/" + SITE_ML
                                                + "/search?include_filters=true&offset=" + offset
                                                + "&search_type=scan&seller_id="
                                                + TypeTools.getCredential(credentials,
                                                                Credenciales.MERCADO_LIBRE_ORDERID)
                                                + status, String.class,
                                                null,
                                                null,
                                                null,
                                                null);
                                respuesta = res.getBody();

                                procesoInfo.addDescripcion(respuesta);
                                String error = "";
                                JSONObject jObject = JSONObject.fromObject(respuesta); // json

                                List<JSONObject> array = jObject.getJSONArray("results");
                                if (array.isEmpty())
                                        return resultDetail;

                                for (int i = 0; i < array.size(); i++) {
                                        ResultadoPublicacionProductoM result = new ResultadoPublicacionProductoM();

                                        JSONObject MLOb = (JSONObject) array.get(i);
                                        String ML = MLOb.getString("id");
                                        Double precio = 0.0D;

                                        try {
                                                precio = MLOb.getDouble("price");
                                        } catch (Exception e) {

                                        }
                                        Double precioRebajado = 0.0D;
                                        try {
                                                precioRebajado = MLOb.getDouble("sale_price");
                                        } catch (Exception e) {
                                                precioRebajado = null;
                                        }

                                        if (precioRebajado == null && precio != null) {
                                                precioRebajado = precio;
                                        }
                                        Double stock = 0.0D;
                                        try {
                                                stock = MLOb.getDouble("available_quantity");
                                        } catch (Exception e) {

                                        }
                                        result.setPrice(precio + "");
                                        result.setPriceDiscount(precioRebajado + "");
                                        result.setStock(stock.longValue() + "");

                                        procesoInfo.addDescripcion("ML ID=" + ML);
                                        result.setIdItemChannel(ML);
                                        List<JSONObject> atributos = MLOb.getJSONArray("attributes");
                                        if (atributos != null && atributos.size() > 0) {
                                                for (int j = 0; j < atributos.size(); j++) {
                                                        JSONObject atr = (JSONObject) atributos.get(j);
                                                        String idA = atr.getString("id");
                                                        if (idA.equals("MODEL")) {
                                                                String value = atr.getString("value_name");
                                                                result.setSkuHermesChannel(value);
                                                        }
                                                }
                                        }
                                        resultDetail.add(result);
                                }
                                offset = offset + 50;

                        } catch (Exception e) {
                                procesoInfo.addDescripcion("", e);
                                continuar = false;
                        }

                }
                procesoInfo.addDescripcion("numero de productos activos en ML=" + resultDetail.size());
                return resultDetail;
        }

        public void feedbackOrders(List<CredentialsSiteChannelDto> credentials, Long idSite,
                        @SuppressWarnings("rawtypes") ProcesoInfo procesoInfo) throws Exception {
                procesoInfo.addDescripcion("feedbackOrders INIT");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DecimalFormat fNumero = new DecimalFormat("#0.00");
                Long idSiteProducto = credentials.get(0).getIdSite();
                Long idSiteVenta = credentials.get(0).getIdSiteCanal();
                Meli mercado = getMeli(credentials, false, idSite, procesoInfo);
                // leermos los pedidos pendientes para feedback
                // TODO a properties
                List<Long> idEstados = (List<Long>) Arrays.asList(idEstadosPedidoFeedback.split(","));
                procesoInfo.addDescripcion("Requesting orders for feedback");
                // Cortar por fecha y/o atributo
                Date fechaCorte = new Date();
                fechaCorte.setTime(fechaCorte.getTime() - (diasFeedback * 24 * 60 * 60 * 1000));
                List<PedidoDto> pedidos = hermesCore.findByIdSiteOriginalAndEstadosAndFecha(idSiteVenta, idEstados,
                                fechaCorte);
                procesoInfo.addDescripcion("Orders for feedback found: " + pedidos.size());
                List<String> originalOrderIdsTratados = new ArrayList<>();
                for (PedidoDto pedidoAlmacen : pedidos) {
                        try {
                                if (pedidoAlmacen.getAtributo(PedidoConstants.ATR_FEEDBACK_ML) == null && "true"
                                                .equals(pedidoAlmacen.getAtributo(PedidoConstants.ATR_FEEDBACK_ML))) {
                                        continue;
                                }
                                procesoInfo.addDescripcion("Checking status for order=" + pedidoAlmacen.getNumero()
                                                + " / " + pedidoAlmacen.getIdPedidoExterno());
                                String result = "";
                                pedidoAlmacen = hermesCore.leerPedidoCompleto(pedidoAlmacen.getIdPedido());
                                JSONObject feedback = new JSONObject();
                                feedback.accumulate("fulfilled", true);
                                feedback.accumulate("feedback", "positive");

                                /*
                                 * FluentStringsMap params = new FluentStringsMap();
                                 * params.add("access_token", mercado.getAccessToken());
                                 */

                                String[] headerQueryParams = { "Accept" };
                                String[] headerQueryParamsValues = { "application/json" };

                                String[] queryParams = { "access_token" };
                                String[] queryParamsValues = { mercado.getAccessToken() };

                                // Response r=null;

                                // Coger cada idOrdenOriginal
                                Boolean exitoTotal = true;
                                String originalOrderId = null;
                                for (PedidoLineaDto linea : pedidoAlmacen.getPedidoLineaList()) {
                                        originalOrderId = (String) linea
                                                        .getAtributo(MercadoLibreConstants.ATR_ORIGINAL_ORDER_ID);
                                        if (StringUtils.isEmpty(originalOrderId)) {
                                                originalOrderId = pedidoAlmacen.getIdPedidoExterno();
                                        }
                                        if (originalOrderIdsTratados.contains(originalOrderId)) {
                                                procesoInfo.addDescripcion("Skipping multiple original order id "
                                                                + originalOrderId);
                                                continue;
                                        } else {
                                                originalOrderIdsTratados.add(originalOrderId);
                                        }
                                        try {
                                                procesoInfo.addDescripcion(
                                                                "######O###### JSON REQUEST /orders/" + originalOrderId
                                                                                + "/feedback: " + feedback.toString());

                                                // r = mercado.post("/orders/"+originalOrderId+"/feedback", params,
                                                // feedback.toString());

                                                ResponseEntity<String> r = mercado.httpPost(
                                                                "/orders/" + originalOrderId + "/feedback", queryParams,
                                                                queryParamsValues, headerQueryParams,
                                                                headerQueryParamsValues, procesoInfo,
                                                                feedback.toString());

                                                procesoInfo.addDescripcion("Order " + pedidoAlmacen.getIdPedidoExterno()
                                                                + " feedback order " + originalOrderId + " result ("
                                                                + r.getStatusCode() + ") =" + r.getBody());
                                                if (r.getStatusCode().value() != 200) {
                                                        exitoTotal = false;
                                                        PedidoEstadoHistoricoDto pedidoHistorico = new PedidoEstadoHistoricoDto(
                                                                        pedidoAlmacen);
                                                        pedidoHistorico.setUsuario("chm");
                                                        pedidoHistorico.setDescripcion(
                                                                        "Error sending feedback order ID="
                                                                                        + originalOrderId);
                                                        pedidoHistorico.setIdEstadoPedido(
                                                                        pedidoAlmacen.getIdEstadoPedido());
                                                        hermesCore.saveHistoricoPedido(pedidoHistorico);
                                                }
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion(
                                                                "Error sending feedback order ID=" + originalOrderId,
                                                                e);
                                                exitoTotal = false;
                                        }
                                }
                                if (exitoTotal) {
                                        PedidoEstadoHistoricoDto pedidoHistorico = new PedidoEstadoHistoricoDto(
                                                        pedidoAlmacen);
                                        pedidoHistorico.setUsuario("chm");
                                        pedidoHistorico.setDescripcion("Positive feedback sent");
                                        pedidoHistorico.setIdEstadoPedido(pedidoAlmacen.getIdEstadoPedido());
                                        hermesCore.saveHistoricoPedido(pedidoHistorico);
                                        pedidoAlmacen.setAtributo(PedidoConstants.ATR_FEEDBACK_ML, "true");
                                        hermesCore.saveAtributosPedido(pedidoAlmacen.getIdPedido(),
                                                        pedidoAlmacen.getAtributos());
                                }

                                // Guardar el atributo

                        } catch (Exception e) {
                                procesoInfo.addDescripcion("ERROR " + getOrderNumber(pedidoAlmacen), e);
                        }
                }
                procesoInfo.addDescripcion("feedbackOrders END");
                return;
        }

        private Boolean sendMailML(List<CredentialsSiteChannelDto> credentials, Meli mercado, String originalOrderId,
                        PedidoDto pedidoAlmacen, String packId,
                        String textoMail,
                        ProcesoInfo procesoInfo, String user_id) throws HermesCoreCallException {
                return sendMailML(credentials, mercado, originalOrderId, pedidoAlmacen, packId, textoMail, procesoInfo,
                                user_id, null);

        }

        private Boolean sendMailML(List<CredentialsSiteChannelDto> credentials, Meli mercado, String originalOrderId,
                        PedidoDto pedidoAlmacen, String packId,
                        String textoMail,
                        ProcesoInfo procesoInfo, String user_id, Long shippingId) throws HermesCoreCallException {

                Boolean exito = true;
                String msgBody = textoMail;
                try {
                        procesoInfo.addDescripcion("enviamos mail=" + textoMail);
                        if (StringUtils.isEmpty(textoMail)) {
                                String mensajeLog = "Mail body not configured for order " + pedidoAlmacen.getNumero()
                                                + " - " + pedidoAlmacen.getIdPedidoExterno();
                                procesoInfo.addDescripcion(mensajeLog);
                                return true;
                        }
                        String sellerId = TypeTools.getCredential(credentials, Credenciales.MERCADO_LIBRE_ORDERID);
                        // Construir llamada
                        JSONObject mensajeJson = new JSONObject();

                        JSONObject msgFrom = new JSONObject();
                        msgFrom.accumulate("user_id", sellerId);
                        msgFrom.accumulate("email", TypeTools.getCredential(credentials, "MERCADO_MAIL_FROM"));
                        mensajeJson.accumulate("from", msgFrom);

                        JSONObject msgTo = new JSONObject();
                        msgTo.accumulate("user_id", user_id);
                        mensajeJson.accumulate("to", msgTo);

                        // Construir cuerpo
                        try {
                                msgBody = msgBody.replace("#NOMBRE#",
                                                pedidoAlmacen.getPedidoDatosPersonales().getNombreDest());
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("Error en mail", e);
                        }
                        try {
                                msgBody = msgBody.replace("#APELLIDO1#",
                                                pedidoAlmacen.getPedidoDatosPersonales().getApellido1Dest());
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("Error en mail", e);
                        }
                        try {
                                msgBody = msgBody.replace("#DIRECCION_COMPLETA#",
                                                pedidoAlmacen.getPedidoDatosPersonales().getDireccionDestinoCompleta());
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("Error en mail", e);
                        }
                        try {
                                msgBody = msgBody.replace("#NUMERO_PEDIDO_ORIGINAL#", originalOrderId);
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("Error en mail", e);
                        }
                        try {
                                msgBody = msgBody.replace("#NUMERO_GUIA#",
                                                (pedidoAlmacen.getNumeroEnvioOperador() != null
                                                                ? pedidoAlmacen.getNumeroEnvioOperador()
                                                                : ""));
                        } catch (Exception e) {
                                procesoInfo.addDescripcion("Error en mail", e);
                        }

                        // Construir adjuntos
                        // https://developers.mercadolibre.com.uy/es_ar/mensajeria-post-venta#Crear-mensajes
                        JSONArray adjuntos = new JSONArray();
                        String fullFilePath = TypeTools.getCredential(credentials, "MERCADO_MAIL_FACTURA_FILEPATH");
                        String pdfPath = "";
                        String xml = "";
                        procesoInfo.addDescripcion("atributos del pedido= " + pedidoAlmacen.getAtributos());
                        try {
                                pdfPath = (String) pedidoAlmacen
                                                .getAtributo(PublicConstants.ATRIBUTO_PEDIDO_PDF_FACTURA_PATH);
                        } catch (Exception e) {
                        }
                        try {
                                xml = (String) pedidoAlmacen.getAtributo(PublicConstants.ATRIBUTO_PEDIDO_XML_FACTURA);
                        } catch (Exception e) {
                        }
                        procesoInfo.addDescripcion("pdfPath= " + pdfPath);
                        procesoInfo.addDescripcion("xml= " + xml);
                        if (!StringUtils.isEmpty(xml)) {
                                fullFilePath = xml;
                        } else {
                                if (msgBody.indexOf("#FACTURA#") != -1 && !StringUtils.isEmpty(fullFilePath)) {
                                        procesoInfo.addDescripcion("Order " + pedidoAlmacen.getIdPedidoExterno()
                                                        + " ruta factura " + fullFilePath);
                                        try {
                                                msgBody = msgBody.replace("#FACTURA#", "");
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("Error en mail", e);
                                        }
                                        fullFilePath = fullFilePath.replace("//", "/");
                                        fullFilePath = fullFilePath.replace("#NUMERO_PEDIDO_ALMACEN#",
                                                        pedidoAlmacen.getNumero());
                                        fullFilePath = fullFilePath.replace("#NUMERO_PEDIDO_PADRE#",
                                                        pedidoAlmacen.getNumeroPedidoOriginal());
                                        fullFilePath = fullFilePath.replace("#NUMERO_PEDIDO_EXTERNO#",
                                                        pedidoAlmacen.getIdPedidoExterno());
                                        procesoInfo.addDescripcion("Order " + pedidoAlmacen.getIdPedidoExterno()
                                                        + " ruta factura  desdpues de replaces" + fullFilePath);
                                }
                        }
                        procesoInfo.addDescripcion("Order " + pedidoAlmacen.getIdPedidoExterno() + " ruta factura "
                                        + fullFilePath);
                        try {
                                fullFilePath = fullFilePath.replace("#NUMERO_PEDIDO_ALMACEN#",
                                                pedidoAlmacen.getNumero());
                        } catch (Exception e) {
                        }
                        try {
                                fullFilePath = fullFilePath.replace("#NUMERO_PEDIDO_PADRE#",
                                                pedidoAlmacen.getNumeroPedidoOriginal());
                        } catch (Exception e) {
                        }
                        try {
                                fullFilePath = fullFilePath.replace("#NUMERO_PEDIDO_EXTERNO#",
                                                pedidoAlmacen.getIdPedidoExterno());
                        } catch (Exception e) {
                        }

                        if (msgBody.indexOf("#FACTURA#") != -1 && !StringUtils.isEmpty(fullFilePath)) {
                                try {
                                        msgBody = msgBody.replace("#FACTURA#", "");
                                } catch (Exception e) {
                                        procesoInfo.addDescripcion("Error en mail", e);
                                }
                        }
                        procesoInfo.addDescripcion("Subiendo factura para pedido " + pedidoAlmacen.getNumero()
                                        + " desde " + fullFilePath);
                        String idFactura = uploadFileML(credentials, mercado, pedidoAlmacen, packId,
                                        MercadoLibreConstants.ATR_FACTURA, fullFilePath,
                                        TypeTools.getCredential(credentials, "MERCADO_MAIL_FACTURA_MIMETYPE"),
                                        procesoInfo, shippingId);
                        if (idFactura != null && !idFactura.startsWith("ERROR")) {
                                adjuntos.add(idFactura);
                        } else {

                                exito = false;
                                /*
                                 * codigoRespuestaOperador = objectShipping.getString("id");
                                 * PedidoEstadoHistoricoDto pedidoHistorico = new
                                 * PedidoEstadoHistoricoDto(pedidoAlmacen);
                                 * pedidoHistorico.setUsuario("chm");
                                 * pedidoHistorico.setDescripcion("Error sending messages order ID="
                                 * +originalOrderId+" bad upload "+idFactura);
                                 * pedidoHistorico.setIdEstadoPedido(pedidoAlmacen.getIdEstadoPedido());
                                 * hermesCore.saveHistoricoPedido(pedidoHistorico);
                                 */
                                return exito;
                        }
                        mensajeJson.accumulate("text", msgBody);
                        mensajeJson.accumulate("attachments", adjuntos);
                        // Llamar
                        FluentStringsMap params = new FluentStringsMap();
                        params.add("access_token", mercado.getAccessToken());
                        procesoInfo.addDescripcion(mensajeJson.toString());

                        ResponseEntity<String> res = null;
                        String respuesta = "";
                        int statusCode = 0;
                        String[] headerQueryParams = { "Authorization", "access_token", "Content-Type", "Accept" };
                        String[] headerQueryParamsValues = { "Bearer " + mercado.getAccessToken(),
                                        mercado.getAccessToken(), "application/xml", "application/xml" };
                        res = HttpClientRestTools.callRestPost(
                                        "https://api.mercadolibre.com/messages/packs/" + originalOrderId + "/sellers/"
                                                        + sellerId,
                                        String.class,
                                        null,
                                        null,
                                        headerQueryParams,
                                        headerQueryParamsValues, mensajeJson.toString(), procesoInfo);
                        respuesta = res.getBody();
                        statusCode = res.getStatusCode().value();
                        if (statusCode < 200 || statusCode >= 300) {
                                throw new Exception();
                        } else {
                                JSONObject respuestaML = JSONObject.fromObject(respuesta);
                                statusCode = respuestaML.getInt("status");
                                if (statusCode < 200 || statusCode >= 300) {

                                }
                        }

                        if (statusCode < 200 || statusCode >= 300) {
                                exito = false;
                                PedidoEstadoHistoricoDto pedidoHistorico = new PedidoEstadoHistoricoDto(pedidoAlmacen);
                                pedidoHistorico.setUsuario("chm");
                                pedidoHistorico.setDescripcion("Error sending messages order ID=" + originalOrderId);
                                pedidoHistorico.setIdEstadoPedido(pedidoAlmacen.getIdEstadoPedido());
                                hermesCore.saveHistoricoPedido(pedidoHistorico);
                                return exito;
                        }
                        // Guardar confirmacion de mail enviado
                        String mensajeLog = "Mail sent: " + msgBody;
                        procesoInfo.addDescripcion(mensajeLog);
                        PedidoEstadoHistoricoDto pedidoHistorico = new PedidoEstadoHistoricoDto(pedidoAlmacen);
                        pedidoHistorico.setUsuario("chm");
                        pedidoHistorico.setDescripcion(mensajeLog);
                        pedidoHistorico.setIdEstadoPedido(pedidoAlmacen.getIdEstadoPedido());
                        hermesCore.saveHistoricoPedido(pedidoHistorico);
                        // pedidoAlmacen.setAtributo(PedidoConstants.ATR_FEEDBACK_ML, "true");
                        // hermesCore.saveAtributosPedido(pedidoAlmacen.getIdPedido(),
                        // pedidoAlmacen.getAtributos());
                } catch (Exception e) {
                        String mensajeLog = "ERROR sending mail: " + msgBody;
                        /*
                         * PedidoEstadoHistoricoDto pedidoHistorico = new
                         * PedidoEstadoHistoricoDto(pedidoAlmacen);
                         * pedidoHistorico.setUsuario("chm");
                         * pedidoHistorico.setDescripcion(mensajeLog);
                         * pedidoHistorico.setIdEstadoPedido(pedidoAlmacen.getIdEstadoPedido());
                         * hermesCore.saveHistoricoPedido(pedidoHistorico);
                         */
                        exito = false;
                        procesoInfo.addDescripcion("", e);
                }
                return exito;
        }

        private String uploadFileML(List<CredentialsSiteChannelDto> credentials, Meli mercado, PedidoDto pedidoAlmacen,
                        String packId, String atrAdjunto,
                        String fullFilePath, String mimeType, ProcesoInfo procesoInfo, Long shippingId)
                        throws HermesCoreCallException {
                String idAttachmentML = "";
                String respuesta = "";
                int statusCode = 0;
                try {
                        procesoInfo.addDescripcion("fullFilePath= " + fullFilePath);
                        JSONObject adjuntos = pedidoAlmacen.getAtributosJSON().getJSONObject("adjuntosML");
                        if (adjuntos == null || adjuntos.isNullObject()) {
                                adjuntos = new JSONObject();
                        }
                        if (StringUtils.isEmpty(atrAdjunto)) {
                                atrAdjunto = fullFilePath;
                        }
                        // Este if impide que subamos dos veces la misma factura. Deshabilitarlo si hay
                        // que sobreescribir facturas
                        if (adjuntos.has(atrAdjunto) && adjuntos.getString(atrAdjunto) != null) {
                                procesoInfo.addDescripcion("Adjunto ya enviado antes, no se tramita: " + atrAdjunto
                                                + "=" + adjuntos.getString(atrAdjunto));
                                return adjuntos.getString(atrAdjunto);
                        }

                        // Construir llamada
                        procesoInfo.addDescripcion("fullFilePath= " + fullFilePath);
                        File file = new File(fullFilePath);
                        String partName = "attachment";
                        Map<String, String> headers = new HashMap<>();
                        if (!MercadoLibreConstants.ATR_FACTURA.equals(atrAdjunto)) {
                                headers.put("content-type", mimeType);
                                partName = "attachment";
                                procesoInfo.addDescripcion("mimeType= " + mimeType);
                        } else {
                                if (mimeType != null && mimeType.contains("xml")) {
                                        headers.put("content-type", mimeType);
                                        procesoInfo.addDescripcion("mimeType= " + mimeType);
                                }
                                partName = "fiscal_document";
                        }

                        procesoInfo.addDescripcion("partName= " + partName);
                        procesoInfo.addDescripcion("mimeType= " + mimeType);
                        procesoInfo.addDescripcion("atrAdjunto= " + atrAdjunto);
                        procesoInfo.addDescripcion("file= " + file.getAbsolutePath());

                        // Llamar
                        FluentStringsMap params = new FluentStringsMap();
                        params.add("access_token", mercado.getAccessToken());
                        params.add("Authorization", "Bearer " + mercado.getAccessToken());
                        if (MercadoLibreConstants.ATR_FACTURA.equals(atrAdjunto)) {
                                // Descomentar esta linea para forzar a borrar la factura antes de mandarla
                                // r = mercado.delete("/packs/"+TypeTools.getCredential(credentials,
                                // packId)+"/fiscal_documents", params);

                                if (mimeType != null && mimeType.contains("xml")) {
                                        String fileText = FileTools.readFile(file.getAbsolutePath(), "UTF-8")
                                                        .replaceAll("\\\\", "");
                                        procesoInfo.addDescripcion("https://api.mercadolibre.com/shipments/"
                                                        + shippingId + "/invoice_data/?access_token="
                                                        + mercado.getAccessToken() + "&siteId=MLB");
                                        procesoInfo.addDescripcion(
                                                        "Authorization=" + "Bearer " + mercado.getAccessToken());
                                        procesoInfo.addDescripcion("access_token=" + mercado.getAccessToken());
                                        procesoInfo.addDescripcion("file=" + fileText);

                                        ResponseEntity<String> res = null;
                                        try {
                                                String[] headerQueryParams = { "Authorization", "access_token",
                                                                "Content-Type", "Accept" };
                                                String[] headerQueryParamsValues = {
                                                                "Bearer " + mercado.getAccessToken(),
                                                                mercado.getAccessToken(), "application/xml",
                                                                "application/xml" };
                                                res = HttpClientRestTools.callRestPost(
                                                                "https://api.mercadolibre.com/shipments/" + shippingId
                                                                                + "/invoice_data/?access_token="
                                                                                + mercado.getAccessToken()
                                                                                + "&siteId=MLB",
                                                                String.class,
                                                                null,
                                                                null,
                                                                headerQueryParams,
                                                                headerQueryParamsValues, fileText, procesoInfo);
                                                respuesta = res.getBody();
                                                statusCode = res.getStatusCode().value();
                                                if (statusCode < 200 || statusCode >= 300) {
                                                        throw new Exception();
                                                } else {
                                                        JSONObject respuestaML = JSONObject.fromObject(respuesta);
                                                        statusCode = respuestaML.getInt("status");
                                                        if (statusCode < 200 || statusCode >= 300) {
                                                                throw new Exception();
                                                        }
                                                }
                                        } catch (Exception ex) {
                                                statusCode = 300;
                                                respuesta = ex.toString();
                                                procesoInfo.addDescripcion("error en primer intento", ex);

                                                try {
                                                        String[] headerQueryParams = { "Authorization", "access_token",
                                                                        "Content-Type", "Accept" };
                                                        String[] headerQueryParamsValues = {
                                                                        "Bearer " + mercado.getAccessToken(),
                                                                        mercado.getAccessToken(), "application/xml",
                                                                        "application/xml" };
                                                        res = HttpClientRestTools.callRestPostAttachment(
                                                                        "https://api.mercadolibre.com/packs/" + packId
                                                                                        + "/fiscal_documents",
                                                                        String.class,
                                                                        null,
                                                                        null,
                                                                        headerQueryParams,
                                                                        headerQueryParamsValues,
                                                                        procesoInfo,
                                                                        file, "fiscal_document");
                                                        respuesta = res.getBody();
                                                        statusCode = res.getStatusCode().value();
                                                        if (statusCode < 200 || statusCode >= 300) {
                                                                throw new Exception();
                                                        } else {
                                                                JSONObject respuestaML = JSONObject
                                                                                .fromObject(respuesta);
                                                                statusCode = respuestaML.getInt("status");
                                                                if (statusCode < 200 || statusCode >= 300) {
                                                                        throw new Exception();
                                                                }
                                                        }
                                                } catch (Exception e) {
                                                        procesoInfo.addDescripcion("error en segundo intento", ex);
                                                        statusCode = 300;
                                                        respuesta = e.toString();
                                                }
                                        }

                                        // r = mercado.postXML("/packs/"+packId+"/invoice_data/&siteId="+sellerId,
                                        // params, fileText, headers);
                                        // r = mercado.postXml("/packs/"+packId+"/fiscal_documents", params, partName,
                                        // file, headers);
                                } else {

                                        /*
                                         * Response r = mercado.post("/packs/"+packId+"/fiscal_documents", params,
                                         * partName, file, headers);
                                         * respuesta=r.getResponseBody();
                                         * statusCode=r.getStatusCode();
                                         */
                                        String pdfFactura = (String) pedidoAlmacen
                                                        .getAtributo(PublicConstants.ATRIBUTO_PEDIDO_PDF_FACTURA);
                                        if (StringUtils.isEmpty(pdfFactura)) {
                                                return null;
                                        }
                                        ResponseEntity<String> res = null;
                                        try {
                                                String[] headerQueryParams = { "Authorization", "access_token" };
                                                String[] headerQueryParamsValues = {
                                                                "Bearer " + mercado.getAccessToken(),
                                                                mercado.getAccessToken() };
                                                res = HttpClientRestTools.callRestPostAttachment(
                                                                "https://api.mercadolibre.com/packs/" + packId
                                                                                + "/fiscal_documents",
                                                                String.class,
                                                                null,
                                                                null,
                                                                headerQueryParams,
                                                                headerQueryParamsValues,
                                                                procesoInfo,
                                                                file, "fiscal_document");
                                                respuesta = res.getBody();
                                                procesoInfo.addDescripcion("respuesta:" + respuesta);
                                                statusCode = res.getStatusCode().value();
                                                /*
                                                 * if(statusCode < 200 || statusCode >= 300){
                                                 * }else {
                                                 * JSONObject respuestaML = JSONObject.fromObject(respuesta);
                                                 * //statusCode =respuestaML.getInt("status");
                                                 * }
                                                 */
                                        } catch (Exception e) {
                                                procesoInfo.addDescripcion("error en envio de facturas:", e);
                                                statusCode = 300;
                                                respuesta = e.toString();

                                        }

                                        // Response r = mercado.post("/packs/"+packId+"/fiscal_documents", params,
                                        // partName, file, headers);
                                        // respuesta=r.getResponseBody();
                                        // statusCode=r.getStatusCode();
                                }
                        } else {
                                Response r = mercado.post("/messages/attachments", params, partName, file, headers);
                                respuesta = r.getResponseBody();
                                statusCode = r.getStatusCode();
                        }

                        procesoInfo.addDescripcion("Order " + pedidoAlmacen.getIdPedidoExterno() + " upload result ("
                                        + statusCode + ") =" + respuesta);

                        if (statusCode < 200 || statusCode >= 300) {
                                idAttachmentML = "ERROR ";
                                try {
                                        idAttachmentML += "upload result (" + statusCode + ") =" + respuesta;
                                } catch (Exception ex) {
                                }
                                ;
                                procesoInfo.addDescripcion(
                                                "ERROR uploading file (" + fullFilePath + ") " + idAttachmentML);
                                PedidoEstadoHistoricoDto pedidoHistorico = new PedidoEstadoHistoricoDto(pedidoAlmacen);
                                pedidoHistorico.setUsuario("chm");
                                pedidoHistorico.setDescripcion(
                                                "Error uploading file (" + fullFilePath + ") =" + idAttachmentML);
                                pedidoHistorico.setIdEstadoPedido(pedidoAlmacen.getIdEstadoPedido());
                                hermesCore.saveHistoricoPedido(pedidoHistorico);
                        } else {
                                JSONObject respuestaML = JSONObject.fromObject(respuesta);
                                idAttachmentML = respuestaML.getJSONArray("ids").getString(0);
                                adjuntos.put(atrAdjunto, idAttachmentML);
                                pedidoAlmacen.setAtributo("adjuntosML", adjuntos);
                                // Guardar confirmacion de mail enviado
                                String mensajeLog = "File uploaded (" + fullFilePath + ") : " + idAttachmentML;
                                procesoInfo.addDescripcion(mensajeLog);
                                PedidoEstadoHistoricoDto pedidoHistorico = new PedidoEstadoHistoricoDto(pedidoAlmacen);
                                pedidoHistorico.setUsuario("chm");
                                pedidoHistorico.setDescripcion(mensajeLog);
                                hermesCore.saveAtributosPedido(pedidoAlmacen.getIdPedido(),
                                                pedidoAlmacen.getAtributos());
                        }

                } catch (Exception e) {
                        idAttachmentML = "ERROR uploading file (" + fullFilePath + ")" + e.getMessage();
                        try {
                                idAttachmentML += " " + " upload result (" + statusCode + ") =" + respuesta;
                        } catch (Exception ex) {
                        }
                        ;

                        PedidoEstadoHistoricoDto pedidoHistorico = new PedidoEstadoHistoricoDto(pedidoAlmacen);
                        pedidoHistorico.setUsuario("chm");
                        pedidoHistorico.setDescripcion(idAttachmentML);
                        pedidoHistorico.setIdEstadoPedido(pedidoAlmacen.getIdEstadoPedido());
                        hermesCore.saveHistoricoPedido(pedidoHistorico);
                        procesoInfo.addDescripcion("ERROR uploading file (" + fullFilePath + ")", e);
                        procesoInfo.addDescripcion("", e);
                }

                return idAttachmentML;
        }

        @Override
        public void crearTablasMaestras(SiteDto site, List<CredentialsSiteChannelDto> credentials,
                        ProcesoInfo procesoInfo, CanalSiteBD cmCanalSite) {
                return;
        }

        @Override
        public void servicesLevel(SiteDto site, List<CredentialsSiteChannelDto> credentials, ProcesoInfo procesoInfo,
                        CanalSiteBD cmCanalSite) throws Exception {
                ResultadoPublicacionM resultadoPublicacion = (ResultadoPublicacionM) procesoInfo
                                .getResultadoPublicacion();

                /*
                 * 
                 * "description":"DOT",
                 * "open_hours":
                 * "Lunes a viernes de 8:30 a 12:30 y de 16:30 a 20:30 hs. - Sábado de 9 a 13 y de 16:30 a 20:30 hs."
                 * ,
                 * "status": "active" | "paused" ,
                 * "phone":
                 * {
                 * "area_code":"011",
                 * "number":"1234"
                 * },
                 * "location":
                 * {
                 * "address_line": "B de Monteagudo 2833 - Florencio Varela - Buenos Aires",
                 * "latitude": -24.2344,
                 * "longitude": -15.122
                 * }
                 * }
                 */

                /*
                 * String activarRetire= TypeTools.getCredential(credentials,
                 * Credenciales.MERCADO_ACTIVAR_RETIRO);
                 * if (activarRetire==null||!activarRetire.equalsIgnoreCase("SI")) return;
                 * 
                 * //recuperamos todas las asociadas
                 * Meli mercado = getMeli(credentials, false,site.getIdSite(), procesoInfo);
                 * 
                 * String sellerId = TypeTools.getCredential(credentials,
                 * Credenciales.MERCADO_LIBRE_ORDERID);
                 * String
                 * urlPath="/users/"+sellerId+"/stores/search?limit=500&offset=0&status=active";
                 * 
                 * Response r;
                 * procesoInfo.addDescripcion(urlPath);
                 * procesoInfo.addDescripcion("token="+mercado.getAccessToken());
                 * FluentStringsMap params = new FluentStringsMap();
                 * params.add("access_token", mercado.getAccessToken());
                 * r = mercado.get(urlPath, params);
                 * 
                 * procesoInfo.addDescripcion(r.getResponseBody());
                 * 
                 * List<String> ids=new ArrayList<>();
                 * try {
                 * JSONObject jObject = JSONObject.fromObject(r.getResponseBody()); // json
                 * JSONArray tiendas=jObject.getJSONArray("results");
                 * for (int j = 0; j < tiendas.size(); j++) {
                 * JSONObject tienda = (JSONObject) tiendas.get(j);
                 * ids.add(tienda.getString("id"));
                 * }
                 * }catch (Exception e) {
                 * procesoInfo.addDescripcion("",e);
                 * }
                 * String urlPathInsert="/users/"+sellerId+"/stores";
                 * //vamos a recuperar los almacenes de clickCollect y vemos cuales tenemos que
                 * dar de alta o baja
                 * List<AlmacenDto>
                 * almacenes=hermesCore.findTiendasClickCollect(site.getIdSite());
                 * for (AlmacenDto almacen:almacenes) {
                 * ResultadoPublicacionProductoM resultado=new ResultadoPublicacionProductoM();
                 * resultado.setIdResultadoPublicacion(resultadoPublicacion.get_id());
                 * String
                 * horario=(String)almacen.getAtributo(PublicConstants.ALMACEN_HORARIO_TEXTO);
                 * if (StringUtils.isEmpty(horario)) {
                 * horario="Lunes a viernes de 8:30 a 12:30"
                 * + ""
                 * + " y de 16:30 a 20:30 hs. - Sábado de 9 a 13 y de 16:30 a 20:30 hs.";
                 * }
                 * String
                 * prefijo=(String)almacen.getAtributo(PublicConstants.ALMACEN_PREFIJO_TFN);
                 * if (StringUtils.isEmpty(prefijo)) {
                 * prefijo="000";
                 * }
                 * String
                 * idEnMl=(String)almacen.getAtributo(PublicConstants.ML_ID_ALMACEN+"_"+site.
                 * getIdSite());
                 * JSONObject productoJson = new JSONObject();
                 * if (StringUtils.isEmpty(idEnMl)) {
                 * procesoInfo.addDescripcion("el almacen="+almacen.getIdPmm()
                 * +" NO tiene id de ML="+idEnMl +", la creamos");
                 * }else {
                 * procesoInfo.addDescripcion("el almacen="+almacen.getIdPmm()
                 * +" tiene como id de ML="+idEnMl +", la actualizamos");
                 * productoJson.put("id", idEnMl);
                 * ids.remove(idEnMl);
                 * }
                 * productoJson.put("description", almacen.getStoreNickNameCC());
                 * productoJson.put("open_hours", horario);
                 * productoJson.put("status", "active");
                 * 
                 * JSONObject phoneJson = new JSONObject();
                 * phoneJson.put("area_code", prefijo);
                 * phoneJson.put("number", almacen.getTelefono());
                 * productoJson.put("phone", phoneJson);
                 * 
                 * JSONObject phoneLocation = new JSONObject();
                 * phoneLocation.put("address_line", almacen.getDireccion()+
                 * " "+almacen.getLocalidad() + " " + almacen.getProvincia());
                 * phoneLocation.put("latitude",
                 * (almacen.getLatitud()!=null?Float.parseFloat(almacen.getLatitud()):0.0));
                 * phoneLocation.put("longitude",
                 * (almacen.getLongitud()!=null?Float.parseFloat(almacen.getLongitud()):0.0));
                 * productoJson.put("location", phoneLocation);
                 * 
                 * procesoInfo.addDescripcion(productoJson.toString());
                 * r = mercado.post(urlPathInsert, params, productoJson.toString());
                 * procesoInfo.addDescripcion(r.getResponseBody());
                 * org.json.JSONObject jObject = new org.json.JSONObject(r.getResponseBody());
                 * // json
                 * String seller_id = null;
                 * try {
                 * seller_id = jObject.getString("id");
                 * } catch (Exception e) {
                 * procesoInfo.addDescripcion("", e);
                 * }
                 * 
                 * resultado.setAlphaCode(almacen.getIdPmm());
                 * if (!StringUtils.isEmpty(seller_id)) {
                 * almacen.setAtributo(PublicConstants.ML_ID_ALMACEN+"_"+site.getIdSite(),
                 * seller_id);
                 * hermesCore.actualizarAlmacenAtributos(almacen.getIdAlmacen(),
                 * JSONObject.fromObject(almacen.getAtributos()));
                 * resultado.setIdItemChannel(seller_id);
                 * resultado.setOk(true);
                 * }else {
                 * resultado.setError(r.getResponseBody());
                 * resultado.setOk(false);
                 * monitor.alert(cmCanalSite.getIdCanal().getIdCanal(),
                 * cmCanalSite.getIdSiteRotulo(), "ServicesLevel");
                 * }
                 * 
                 * publicationResultService.guardarResultadoProducto(resultado, null,
                 * procesoInfo);
                 * }
                 * 
                 * //las que queden en la lista las eliminamos
                 * for (String id:ids) {
                 * procesoInfo.addDescripcion(" eliminamos la tienda="+id);
                 * r = mercado.delete("/users/"+sellerId+"/stores/"+id, params);
                 * procesoInfo.addDescripcion(r.getResponseBody());
                 * }
                 * 
                 * mercado.close();
                 * monitor.event(cmCanalSite.getIdCanal().getIdCanal(),
                 * cmCanalSite.getIdSiteRotulo(), "ServicesLevel");
                 */
                return;
        }

        @Override
        public void actualizarFotos(SiteDto site, List<CredentialsSiteChannelDto> credentials,
                        List<ProductoCollectionDto> productos, ProcesoInfo procesoInfo, CanalSiteBD cmCanalSite)
                        throws Exception {
                // TODO Auto-generated method stub

        }

        public void pruebasMail(SiteDto site, List<CredentialsSiteChannelDto> credentials,
                        List<ProductoCollectionDto> productos, ProcesoInfo procesoInfo, CanalSiteBD cmCanalSite)
                        throws Exception {

                // TODO Auto-generated method stub
                Meli mercado = getMeli(credentials, false, site.getIdSite(), procesoInfo);

                PedidoDto pedidoAlmacen = hermesCore.leerPedidoCompleto(3333638L);
                String packId = "2464805261";
                Boolean mandado = sendMailML(credentials, mercado, pedidoAlmacen.getIdPedidoExterno(), pedidoAlmacen,
                                packId,
                                TypeTools.getCredential(credentials, MercadoLibreConstants.MERCADO_MAIL_TEXTO_CREADO),
                                procesoInfo, "");

                // pedidoAlmacen.setAtributo(nombreAtributoMailCreado, ""+mandado);
                // hermesCore.saveAtributosPedido(pedidoAlmacen.getIdPedido(),
                // pedidoAlmacen.getAtributos());

        }

        public ResultadoPublicacionProductoM getProduct(String idProductoCanal, String idSite, Meli mercado,
                        List<CredentialsSiteChannelDto> credentials,
                        ProcesoInfo procesoInfo) throws MeliException {
                return getProduct(idProductoCanal, idSite, mercado, credentials, procesoInfo, false);
        }

        public ResultadoPublicacionProductoM getProduct(String idProductoCanal, String idSite, Meli mercado,
                        List<CredentialsSiteChannelDto> credentials,
                        ProcesoInfo procesoInfo, Boolean full) throws MeliException {
                String sellerId = TypeTools.getCredential(credentials, Credenciales.MERCADO_LIBRE_ORDERID);
                String parametros = "";
                if (!full)
                        parametros = "?attributes=variations";
                String urlPath = "/items/{item}" + parametros;
                urlPath = urlPath.replace("{item}", idProductoCanal);
                ResultadoPublicacionProductoM resultDetail = new ResultadoPublicacionProductoM();

                String[] queryParams = { "access_token", "limit" };
                String[] queryParamsValues = { mercado.getAccessToken(), "5000" };

                String[] headerQueryParams = { "Accept" };
                String[] headerQueryParamsValues = { "application/json" };
                // Response r = mercado.get(urlPath);
                try {
                        String respuesta = mercado.httpGet(urlPath, queryParams, queryParamsValues, headerQueryParams,
                                        headerQueryParamsValues);
                        procesoInfo.addDescripcion(respuesta);
                        String error = "";
                        org.json.JSONObject jObject = new org.json.JSONObject(respuesta); // json
                        if (full) {
                                resultDetail.setIdItemChannel(jObject.getString("id"));
                                try {
                                        resultDetail.setSkuHermesChannel(jObject.getString("seller_custom_field"));
                                } catch (Exception e) {
                                        // procesoInfo.addDescripcion("",e);
                                }
                                resultDetail.setAlphaCode(jObject.getString("status"));

                        }
                        org.json.JSONArray array = jObject.getJSONArray("variations");
                        List<ResultadoPublicacionProductoVariantM> resultsSub = new ArrayList<ResultadoPublicacionProductoVariantM>();
                        for (int i = 0; i < array.length(); i++) {
                                org.json.JSONObject oVariante = (org.json.JSONObject) array.get(i);
                                // String seller_custom_field= oVariante.getString("seller_custom_field");
                                // procesoInfo.addDescripcion("seller_custom_field="+seller_custom_field);
                                String idV = null;
                                try {
                                        idV = oVariante.getString("id");
                                } catch (Exception e) {
                                        Long idL = oVariante.getLong("id");
                                        idV = idL + "";
                                }

                                procesoInfo.addDescripcion("idV=" + idV);
                                org.json.JSONArray arrayCombinations = oVariante.getJSONArray("attribute_combinations");
                                String seller_custom_field = "";
                                for (int j = 0; j < arrayCombinations.length(); j++) {
                                        org.json.JSONObject oCombination = (org.json.JSONObject) arrayCombinations
                                                        .get(j);
                                        String idC = oCombination.getString("id");
                                        procesoInfo.addDescripcion("idC=" + idC);
                                        if (idC.equalsIgnoreCase("SIZE")) {
                                                String value_name = oCombination.getString("value_name");
                                                // procesoInfo.addDescripcion("value_name="+value_name);
                                                seller_custom_field = value_name;
                                        }
                                }

                                ResultadoPublicacionProductoVariantM variante = new ResultadoPublicacionProductoVariantM();
                                ;
                                variante.setIdItemChannel(idV);
                                variante.setSkuHermesChannel(seller_custom_field);
                                resultsSub.add(variante);
                        }
                        resultDetail.setVariants(resultsSub);
                } catch (Exception e) {
                        procesoInfo.addDescripcion("", e);
                }
                // procesoInfo.addDescripcion(BeanDescriber.stringify(resultDetail));
                return resultDetail;
        }

        private ResultadoPublicacionProductoM actualizarIdML(ProductoCollectionDto producto, SiteDto site,
                        String idProductoCanal,
                        Meli mercado, List<CredentialsSiteChannelDto> credentials,
                        ProcesoInfo procesoInfo) throws HermesCoreCallException, MeliException {
                ResultadoPublicacionProductoM resultDetalle = getProduct(credentials, idProductoCanal,
                                site.getIdSite().toString(), mercado, procesoInfo);

                for (SubProductoDto sb : producto.getSubProductos()) {
                        sb.setAtributo("ID_ITEM_ML", "");
                }

                // actualizamos sub productos
                List<ResultadoPublicacionProductoVariantM> variantes = resultDetalle.getVariants();
                if (variantes != null && variantes.size() > 0) {
                        for (ResultadoPublicacionProductoVariantM variante : variantes) {

                                String talla = variante.getSkuHermesChannel();
                                if (StringUtils.isEmpty(talla)) {
                                        talla = "Talla unica";
                                }
                                String idArticulo = producto.getIdArticuloModalia();

                                procesoInfo.addDescripcion("idArticulo=" + idArticulo);
                                procesoInfo.addDescripcion("talla=" + talla);

                                // SubProductoDto subProducto =
                                // hermesCore.leerSubProductoPorIdSiteIdArticuloModalia(idSiteProducto,
                                // seller_custom_field);
                                for (SubProductoDto sb : producto.getSubProductos()) {
                                        // sb.setAtributo("ID_ITEM_ML","");
                                        // procesoInfo.addDescripcion("sb.getValor()=" +sb.getValor());
                                        // procesoInfo.addDescripcion("sb.getTallajeOriginalCliente()="
                                        // +sb.getTallajeOriginalCliente());
                                        // procesoInfo.addDescripcion("entontramos?="
                                        // +(sb.getValor().equals(talla)||sb.getTallajeOriginalCliente().equals(talla)));
                                        // procesoInfo.addDescripcion("idML=" + variante.getIdItemChannel());
                                        if (sb.getValor().equals(talla)
                                                        || sb.getTallajeOriginalCliente().equals(talla)) {
                                                if (sb != null && variante.getIdItemChannel() != null) {
                                                        sb.setAtributo("ID_ITEM_ML", variante.getIdItemChannel());

                                                }
                                        }
                                }
                        }

                        for (SubProductoDto sb : producto.getSubProductos()) {
                                // procesoInfo.addDescripcion("sb.getIdSubProducto()=" +sb.getAtributos());
                                hermesCore.actualizarSubProductoAtributos(sb.getIdSubProducto(),
                                                JSONObject.fromObject(sb.getAtributos()));

                        }
                }
                return resultDetalle;
        }

        private Long getStockProducto(ProductoCollectionDto pr) {
                Long stock = 0L;
                for (SubProductoDto sub : pr.getSubProductos()) {
                        stock = stock + sub.getStockEnSite();
                }
                return stock;
        }

        private String getEanProducto(ProductoCollectionDto pr) {
                return pr.getSubProductos().get(0).getEan();
        }

        // metodos para nueva API DE ML

        /**
         * Metodo para actualizar stock masivo. la ejecucion no termina hasta que
         * mercadolibre da un OK
         */
        /*
         * public void updateStocksNew(SiteDto site, List<CredentialsSiteChannelDto>
         * credentials,
         * List<ProductoCollectionDto> productos, ProcesoInfo procesoInfo, CanalSiteBD
         * cmCanalSite) throws Exception {
         * procesoInfo.addDescripcion("updateStock INIT");
         * procesoInfo.addDescripcion("Products read from publications: "+productos.size
         * ());
         * SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
         * ResultadoPublicacionM resultadoPublicacion = (ResultadoPublicacionM)
         * procesoInfo.getResultadoPublicacion();
         * resultadoPublicacion.setCountProducts(new Long(productos.size()));
         * 
         * int total = productos.size();
         * int buenos = 0;
         * 
         * 
         * Meli mercado = getNewMeli(credentials, false,site.getIdSite(), procesoInfo);
         * 
         * // insertamos
         * int contador = 0;
         * 
         * if (productos.size() > 0) {
         * for (ProductoCollectionDto producto : productos) {
         * contador++;
         * mercado = getMeli(credentials, false,site.getIdSite(), procesoInfo);
         * ResultadoPublicacionProductoM resultDetalle = new
         * ResultadoPublicacionProductoM();
         * resultDetalle.setIdProducto(producto.getIdProducto());
         * resultDetalle.setAlphaCode(producto.getCodigoAlfa());
         * resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
         * resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
         * try {
         * String productIdML = getIdProductoML(site, cmCanalSite, producto);
         * 
         * procesoInfo.addDescripcion("Preparing product ID "+producto.getIdProducto()
         * +" ML="+productIdML+" ("+contador+"/"+productos.size()+")");
         * CanalSiteProductoM canalSiteProducto =
         * buscarProducto(producto.getIdProducto(), cmCanalSite);
         * //TODO mirar a ver si aqui va a haber que mandar un 0 si no esta activo
         * if (productIdML==null ||
         * canalSiteProducto==null||!canalSiteProducto.getPublicado()) {
         * resultDetalle.setIdProducto(producto.getIdProducto());
         * resultDetalle.setAlphaCode(producto.getCodigoAlfa());
         * resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
         * resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
         * resultDetalle.setOk(false);
         * resultDetalle.setError("Product not published or inactive");
         * publicationResultService.guardarResultadoProductoPositivo(resultDetalle,
         * canalSiteProducto);
         * resultadoPublicacion.setCountProducts(resultadoPublicacion.getCountProducts()
         * +1);
         * continue;
         * }
         * resultadoPublicacion.getProducts().add(resultDetalle);
         * // leemos stock y productos
         * String garantia = TypeTools.getCredential(credentials,
         * "mercado.libre.garantia");
         * 
         * resultDetalle = updateVariantesInternalNew(producto,
         * site.getIdSite().toString(), site, true, productIdML, mercado, garantia,
         * procesoInfo,resultDetalle,false, credentials);
         * 
         * if (!resultDetalle.isOk()) {
         * procesoInfo.addDescripcion("Tenemos error ene l producto="+producto.
         * getIdProducto()+", vamos a actualizar ids y reintentamos");
         * resultDetalle= actualizarIdML(producto, site, productIdML,
         * site.getIdSite().toString(), mercado, credentials, procesoInfo);
         * resultDetalle = updateVariantesInternalNew(producto,
         * site.getIdSite().toString(), site, true, productIdML, mercado, garantia,
         * procesoInfo,resultDetalle,false, credentials);
         * }
         * 
         * procesoInfo.addDescripcion("Completed product ID "+producto.getIdProducto()
         * +" ("+contador+"/"+productos.size()+")");
         * resultDetalle.setIdProducto(producto.getIdProducto());
         * resultDetalle.setAlphaCode(producto.getCodigoAlfa());
         * resultDetalle.setIdResultadoPublicacion(resultadoPublicacion.get_id());
         * resultDetalle.setSkuHermesChannel(producto.getIdArticuloModalia());
         * //resultDetalle.setOk(true);
         * //resultDetalle.setError("");
         * 
         * // actualizamos sub productos
         * List<ResultadoPublicacionProductoVariantM> variantes =
         * resultDetalle.getVariants();
         * for (ResultadoPublicacionProductoVariantM variante : variantes) {
         * procesoInfo.addDescripcion("variante=" + BeanDescriber.stringify(variante));
         * 
         * if (!StringUtils.isEmpty(variante.getIdItemChannel())) {
         * SubProductoDto sb =
         * hermesCore.leerSubProductoPorIdSubProducto(variante.getIdSubProducto());
         * //sb.setAtributo("ID_ITEM_ML", variante.getIdItemChannel());
         * //variante.setStock(sb.getStockEnSite()+"");
         * variante.setEan(sb.getEan());
         * variante.setSize(sb.getTallajeOriginalCliente());
         * variante.setSkuHermesChannel(""+sb.getIdSubProducto());
         * variante.setOk(true);
         * variante.setError("");
         * }
         * }
         * publicationResultService.guardarResultadoProductoPositivo(resultDetalle,
         * canalSiteProducto);
         * resultadoPublicacion.setCountProducts(resultadoPublicacion.getCountProducts()
         * +1);
         * 
         * } catch (Exception e) {
         * procesoInfo.addDescripcion("",e);
         * procesoInfo.addDescripcion("Error mercadolibre: ", e);
         * resultDetalle.setOk(false);
         * resultDetalle.setError(e.toString());
         * publicationResultService.guardarResultadoProducto(resultDetalle, null,
         * procesoInfo);
         * }
         * }
         * 
         * }
         * mercado.close();
         * procesoInfo.addDescripcion("endDate "+ sdf.format(new Date()));
         * procesoInfo.setResultadosOk(new Long(buenos));
         * procesoInfo.addDescripcion("Updated "+buenos+" of "+total);
         * procesoInfo.setResultadoPublicacion(resultadoPublicacion);
         * procesoInfo.addDescripcion("updateStock END");
         * return;
         * }
         */

        /*
         * public ResultadoPublicacionProductoM
         * updateVariantesInternalNew(ProductoCollectionDto producto, String string,
         * SiteDto site, boolean b, String itemId, Meli mercado, String garantia,
         * ProcesoInfo procesoInfo,ResultadoPublicacionProductoM resultDetail, Boolean
         * price,List<CredentialsSiteChannelDto> credentials) {
         * //String result = "";
         * 
         * try {
         * // Item item = new Item();
         * procesoInfo.addDescripcion("item=" + itemId);
         * //FluentStringsMap params = new FluentStringsMap();
         * JSONObject productoJson = new JSONObject();
         * Boolean tieneId=false;
         * Long stock=0L;
         * for (SubProductoDto subProducto:producto.getSubProductos()){
         * stock=subProducto.getStockEnSite();
         * try {
         * if (stock==null||stock<0){
         * stock=0L;
         * }
         * if (!producto.getActivo()){
         * stock=0L;
         * }
         * } catch (Exception e) {
         * procesoInfo.addDescripcion("########O######### Error: ",e);
         * if (stock==null||stock<0){
         * stock=0L;
         * }
         * }
         * String atr = (String) subProducto.getAtributoStr("ID_ITEM_ML");
         * if (!StringUtils.isEmpty(atr)) {
         * tieneId=true;
         * }
         * }
         * RestClientApi restClientApi = new
         * RestClientApi(mercado.getDefaultApiClient());
         * if (tieneId &&!site.getIdSite().equals(633L)&&!site.getIdSite().equals(611L))
         * {
         * JSONArray variations =variationStock(producto,
         * resultDetail,procesoInfo,price);
         * //item.setVariations(variations);
         * productoJson.put("variations", variations);
         * }else {
         * //item.setAvailableQuantity(stock+"");
         * productoJson.put("available_quantity", stock+"");
         * }
         * 
         * //params.add("access_token", mercado.getAccessToken());
         * 
         * Response r;
         * procesoInfo.addDescripcion(productoJson.toString());
         * String id = "";
         * //r = mercado.put("/items/" + itemId, params, productoJson.toString());
         * 
         * 
         * //item.availableQuantity(stock.toString());
         * 
         * String resource = "/items"; // resource like items, search, category etc
         * Object result = restClientApi.resourcePut(resource,
         * mercado.getRefreshToken(), productoJson);
         * procesoInfo.addDescripcion(result.toString());
         * 
         * String error = "";
         * JSONObject jObject = JSONObject.fromObject(result); // json
         * String seller_id = null;
         * String status = null;
         * String message = null;
         * String errorMsg = null;
         * 
         * try {
         * seller_id = jObject.getString("id");
         * } catch (Exception e) {
         * procesoInfo.addDescripcion("", e);
         * }
         * try {
         * status = jObject.getString("status");
         * } catch (Exception e) {
         * procesoInfo.addDescripcion("", e);
         * }
         * try {
         * message = jObject.getString("message");
         * } catch (Exception e) {
         * }
         * try {
         * errorMsg = jObject.getString("error");
         * } catch (Exception e) {
         * }
         * 
         * if (message != null &&
         * (message.equals("expired_token")||message.equals("invalid_token")||message.
         * equalsIgnoreCase("Invalid token"))) {
         * hashMelis.remove(site.getIdSite());
         * resultDetail.setOk(false);
         * resultDetail.setError(message);
         * return resultDetail;
         * }
         * 
         * procesoInfo.addDescripcion("seller_id=" + seller_id);
         * procesoInfo.addDescripcion("status=" + status);
         * procesoInfo.addDescripcion("message=" + message);
         * procesoInfo.addDescripcion("error=" + errorMsg);
         * 
         * if (StringUtils.isEmpty(seller_id)) {
         * resultDetail.setOk(false);
         * resultDetail.setError(result.toString());
         * return resultDetail;
         * }
         * JSONArray array = jObject.getJSONArray("variations");
         * for (int i = 0; i < array.size(); i++) {
         * JSONObject oVariante = (JSONObject) array.get(i);
         * procesoInfo.addDescripcion("variante=" + oVariante.toString());
         * String seller_custom_field = oVariante.getString("seller_custom_field");
         * procesoInfo.addDescripcion("seller_custom_field=" + seller_custom_field);
         * String idV = null;
         * try {
         * idV = oVariante.getString("id");
         * } catch (Exception e) {
         * Long idL = oVariante.getLong("id");
         * idV = idL + "";
         * }
         * procesoInfo.addDescripcion("idV=" + idV);
         * List<ResultadoPublicacionProductoVariantM> variantes =
         * resultDetail.getVariants();
         * for (ResultadoPublicacionProductoVariantM varian : variantes) {
         * if
         * (varian!=null&&varian.getSkuHermesChannel()!=null&&varian.getSkuHermesChannel
         * ().equals(seller_custom_field)) {
         * varian.setIdItemChannel(idV);
         * }
         * }
         * }
         * resultDetail.setIdItemChannel(seller_id);
         * resultDetail.setSkuHermesChannel(producto.getIdArticuloModalia());
         * resultDetail.setOk(true);
         * } catch (Exception e) {
         * procesoInfo.addDescripcion("",e);
         * procesoInfo.addDescripcion("", e);
         * resultDetail.setError(e.getMessage());
         * resultDetail.setOk(false);
         * }
         * return resultDetail;
         * }
         * 
         * 
         * 
         * public Meli getNewMeli(List<CredentialsSiteChannelDto> credentials, boolean
         * force, Long idSite, ProcesoInfo procesoInfo) throws Exception {
         * if (hashMelis.get(idSite) != null) {
         * procesoInfo.addDescripcion("recuperamos de hash");
         * return hashMelis.get(idSite);
         * }
         * return getNewAccessToken(null, credentials,idSite, procesoInfo);
         * }
         * 
         * public Meli getNewAccessToken(Meli mercado, List<CredentialsSiteChannelDto>
         * credentials, Long idSite, ProcesoInfo procesoInfo) throws Exception {
         * if (mercado==null) {
         * Long clientId = Long.parseLong(TypeTools.getCredential(credentials,
         * Credenciales.MERCADO_CLIENT_ID));
         * String clientSecret = TypeTools.getCredential(credentials,
         * Credenciales.MERCADO_CLIENT_SECRET);
         * mercado = new Meli(clientId, clientSecret);
         * mercado.refreshToken = TypeTools.getCredential(credentials,
         * Credenciales.MERCADO_REFRESH_TOKEN);
         * //mercado.code = TypeTools.getCredential(credentials,
         * Credenciales.MERCADO_AUTH_TOKEN);
         * ApiClient defaultClient =mercado.newRefreshAccessToken();
         * mercado.setDefaultApiClient(defaultClient);
         * procesoInfo.addDescripcion("code="+mercado.code);
         * procesoInfo.addDescripcion("refreshToken="+mercado.refreshToken);
         * procesoInfo.addDescripcion("access token="+mercado.getAccessToken());
         * CredentialsSiteChannelDto refreshToken =
         * TypeTools.getCredentialBean(credentials,Credenciales.MERCADO_REFRESH_TOKEN);
         * if (!StringUtils.isEmpty(mercado.refreshToken)) {
         * refreshToken.setValue(mercado.refreshToken);
         * TypeTools.updateCredentialBean(credentials,
         * Credenciales.MERCADO_REFRESH_TOKEN, mercado.refreshToken);
         * credentialsService.saveCredential(refreshToken);
         * }else{
         * procesoInfo.addDescripcion("Refresh token is null");
         * }
         * CredentialsSiteChannelDto accessToken =
         * TypeTools.getCredentialBean(credentials,Credenciales.MERCADO_AUTH_TOKEN);
         * if (!StringUtils.isEmpty(mercado.getAccessToken())) {
         * accessToken.setValue(mercado.getAccessToken());
         * TypeTools.updateCredentialBean(credentials, Credenciales.MERCADO_AUTH_TOKEN,
         * mercado.getAccessToken());
         * credentialsService.saveCredential(accessToken);
         * }else {
         * procesoInfo.addDescripcion("Access token is null");
         * 
         * }
         * credentials = credentialsService.getCredentialsBySiteChannel(idSite);
         * hashMelis.put(idSite, mercado);
         * }else {
         * ApiClient defaultClient =
         * com.hermes.chm.integraciones.marketplace.mercadolibre.meli.Configuration.
         * getDefaultApiClient();
         * defaultClient.setBasePath("https://api.mercadolibre.com");
         * mercado.setDefaultApiClient(defaultClient);
         * }
         * return mercado;
         * }
         */
        private String leerPoliticaDelModeloDeEnvio(Long idPublicacion, String politica, Long idAtributoModeloCanal,
                        ProcesoInfo procesoInfo) {

                politica = null;
                try {
                        // procesoInfo.addDescripcion("idPublicacion="+idPublicacion+"|"+"idAtributoModeloCanal="+idAtributoModeloCanal);
                        List<ModeloBD> modelos = modeloRepository.findAllByIdPublicacion(idPublicacion);
                        ModeloBD modeloEnvio = null;
                        if (modelos != null && !modelos.isEmpty()) {
                                for (int i = 0; i < modelos.size(); i++) {
                                        if (modelos.get(i).getIdTipoModelo()
                                                        .getIdTipoModelo() == TipoModelo.ENVIO_value) {
                                                modeloEnvio = modelos.get(i);
                                                break;
                                        }
                                }
                        }
                        // procesoInfo.addDescripcion("modeloEnvio="+modeloEnvio);
                        if (modeloEnvio != null) {
                                List<ModeloAtributoBD> atributos = modeloAtributoRepository
                                                .findAllByIdModelo(modeloEnvio);
                                // procesoInfo.addDescripcion("modeloEnvio="+atributos);
                                for (int i = 0; i < atributos.size(); i++) {
                                        // procesoInfo.addDescripcion("atributos.get(i).getIdAtributoModeloCanal()="+atributos.get(i).getIdAtributoModeloCanal());
                                        // procesoInfo.addDescripcion("atributos.get(i).getValor()="+atributos.get(i).getValor());
                                        if (atributos.get(i).getIdAtributoModeloCanal()
                                                        .getIdAtributoModeloCanal() == idAtributoModeloCanal &&
                                                        !atributos.get(i).getValor().trim().isEmpty()) {
                                                politica = atributos.get(i).getValor().trim();
                                                break;
                                        }
                                }
                        }
                } catch (Exception e) {
                        // TODO
                        procesoInfo.addDescripcion(
                                        "Error recuperando las políticas de envío y devolución definidas en el modelo.");
                }

                return politica;
        }

        private String getPaidAmount(JSONObject order) {
                return (String) order.getJSONArray("payments").stream()
                                .map(paymnet -> ((JSONObject) paymnet).getDouble("total_paid_amount"))
                                .reduce((a, b) -> ((Double) a) + ((Double) b))
                                .map(Object::toString)
                                .orElse(null);
        }

        private String getTotalShippingCost(JSONObject json, ProcesoInfo processInfo) {
                boolean isOk = false;
                Double total = 0D;

                try {
                        JSONArray payments = json.getJSONArray("payments");
                        for (int index = 0; index < payments.size(); index++) {
                                JSONObject payment = payments.getJSONObject(index);
                                double subTotal = payment.getDouble("shipping_cost");
                                total = total + subTotal;
                        }
                        isOk = true;
                        processInfo.addDescripcion("Total shipping cost: [" + total.toString() + "]");
                } catch (Exception ex) {
                        processInfo.addDescripcion("Fail to get shipping cost.", ex);
                        isOk = false;
                }

                return isOk ? total.toString() : "";
        }

        @Override
        public void refund(SiteDto site, List<CredentialsSiteChannelDto> credentials, ProcesoInfo procesoInfo,
                        CanalSiteBD cmCanalSite) throws Exception {
                // TODO Auto-generated method stub

        }

        private boolean hasVariation(SiteDto site, ProcesoInfo<?> procesoInfo,
                        List<CredentialsSiteChannelDto> credentials, ProductoCollectionDto product) {
                boolean hasVariation = false;
                boolean isBrazilEnvironment = this.productTools.getBrazilianEnvironment(credentials);
                if (isBrazilEnvironment) {
                        procesoInfo.addDescripcion("Brazilian check variation.");
                        hasVariation = this.productTools.hasVariations(product.getIdPmm(), product.getIdSite(),
                                        procesoInfo, credentials);
                } else {
                        procesoInfo.addDescripcion("No Brazilian check variation.");
                        hasVariation = !site.isTOTVSWinThorERP();
                }

                return hasVariation;
        }

        private String getAditionalInfoValue(JSONArray aditionalInfo, String key) {
                int index = 0;
                String value = "";
                while (index < aditionalInfo.size()) {
                        JSONObject info = aditionalInfo.getJSONObject(index);
                        String type = info.getString("type");
                        if (type.equalsIgnoreCase(key)) {
                                value = info.getString("value");
                                break;
                        }
                        index++;
                }
                return value;
        }
}
