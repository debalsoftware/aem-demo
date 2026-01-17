/*
 * package com.aem.demo.core.servlets;
 * 
 * import java.io.File; import java.io.FileInputStream; import
 * java.io.FileNotFoundException; import java.io.InputStream; import
 * java.util.ArrayList; import java.util.HashMap; import java.util.Iterator;
 * import java.util.List; import java.util.Map; import java.util.Objects;
 * 
 * import javax.jcr.Session; import javax.servlet.Servlet; import
 * javax.xml.parsers.DocumentBuilder; import
 * javax.xml.parsers.DocumentBuilderFactory; import
 * javax.xml.parsers.ParserConfigurationException; import
 * javax.xml.transform.OutputKeys; import javax.xml.transform.Transformer;
 * import javax.xml.transform.TransformerConfigurationException; import
 * javax.xml.transform.TransformerException; import
 * javax.xml.transform.TransformerFactory; import
 * javax.xml.transform.dom.DOMSource; import
 * javax.xml.transform.stream.StreamResult;
 * 
 * import org.apache.sling.api.SlingHttpServletRequest; import
 * org.apache.sling.api.SlingHttpServletResponse; import
 * org.apache.sling.api.resource.Resource; import
 * org.apache.sling.api.resource.ResourceResolver; import
 * org.apache.sling.api.servlets.HttpConstants; import
 * org.apache.sling.api.servlets.SlingAllMethodsServlet; import
 * org.osgi.service.component.annotations.Component; import
 * org.osgi.service.component.annotations.Reference; import
 * org.w3c.dom.Document; import org.w3c.dom.Element;
 * 
 * import com.day.cq.dam.api.AssetManager; import
 * com.day.cq.search.PredicateGroup; import com.day.cq.search.Query; import
 * com.day.cq.search.QueryBuilder; import com.day.cq.search.result.SearchResult;
 * 
 * @Component(service = Servlet.class, property = { "sling.servlet.paths=" +
 * "/bin/damData", "sling.servlet.methods=" + HttpConstants.METHOD_GET }) public
 * class WriteDatatoDamServlet extends SlingAllMethodsServlet {
 * 
 * @Reference private QueryBuilder queryBuilder;
 * 
 * protected void doGet(SlingHttpServletRequest slingHttpServletRequest,
 * SlingHttpServletResponse slingHttpServletResponse) {
 * 
 * Map<String, String> map = new HashMap<String, String>(); List<String>
 * arrayList = new ArrayList<String>();
 * 
 * try (ResourceResolver resourceResolver =
 * slingHttpServletRequest.getResourceResolver()) {
 * 
 * Session session = resourceResolver.adaptTo(Session.class);
 * 
 * if (Objects.nonNull(session)) {
 * 
 * map.put("path", "/content/dam/we-retail/en/people"); map.put("type",
 * "dam:Asset"); map.put("p.limit", "-1");
 * 
 * Query searchquery = queryBuilder.createQuery(PredicateGroup.create(map),
 * session);
 * 
 * 
 * SearchResult searchResult = searchquery.getResult();
 * 
 * 
 * Iterator<Resource> resources = searchResult.getResources(); while
 * (resources.hasNext()) {
 * 
 * Resource searchResultResource = resources.next();
 * 
 * String searchrseourcePath = searchResultResource.getPath();
 * arrayList.add(searchrseourcePath);
 * 
 * } writeToxml(arrayList, resourceResolver); }
 * 
 * }
 * 
 * }
 * 
 * private void writeToxml(List<String> arrayList, ResourceResolver
 * resourceResolver) { AssetManager assetManager =
 * resourceResolver.adaptTo(AssetManager.class); String filename =
 * "Asset".concat(".xml"); File file = new File(filename);
 * DocumentBuilderFactory documentFactory =
 * DocumentBuilderFactory.newInstance();
 * 
 * DocumentBuilder documentBuilder = null; try { documentBuilder =
 * documentFactory.newDocumentBuilder(); Document document =
 * documentBuilder.newDocument(); Element root =
 * document.createElement("AssetPaths"); document.appendChild(root); for (String
 * path : arrayList) { Element assetPath = document.createElement("AssetPath");
 * root.appendChild(assetPath);
 * assetPath.appendChild(document.createTextNode(path)); } TransformerFactory
 * transformerFactory = TransformerFactory.newInstance(); Transformer
 * transformer = transformerFactory.newTransformer();
 * transformer.setOutputProperty(OutputKeys.INDENT, "yes"); DOMSource domSource
 * = new DOMSource(document); StreamResult streamResult = new
 * StreamResult(file); transformer.transform(domSource, streamResult);
 * InputStream fileInputStream = new FileInputStream(file);
 * 
 * assetManager.createAsset("/content/dam/we-retail/en/people/" + filename,
 * fileInputStream, "application/xml", true); } catch
 * (ParserConfigurationException e) {
 * 
 * e.printStackTrace(); } catch (TransformerConfigurationException e) {
 * 
 * e.printStackTrace(); } catch (TransformerException e) {
 * 
 * e.printStackTrace(); } catch (FileNotFoundException e) {
 * 
 * e.printStackTrace(); }
 * 
 * }
 * 
 * }
 */