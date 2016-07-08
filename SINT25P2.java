import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.xpath.*;
import org.xml.sax.*;
import javax.xml.xpath.XPathConstants;
import java.lang.*;
// imports XPATH
import javax.xml.xpath.*;
import java.lang.*;
import java.util.*;
//Fin imports para xml
class SimpleErrorHandler implements ErrorHandler {
    public void warning(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());
    }

    public void error(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());
    }

    public void fatalError(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());
    }
}

public class SINT25P2 extends HttpServlet {
    public DocumentBuilderFactory dbf = null;
    public DocumentBuilder db = null;
    public Document doc , doc_aux;
    public Element root = null;
    public PrintWriter out = null;
    public XPath xPath = null;
    public String Dir = "http://localhost:8025/jose/mml2001.xml";
    // Public String Dir = "http://clave.det.uvigo.es:8080/~sintprof/14-15/p2/teleco.xml";
    public String http = "http://";
    public HashMap<String,Document> listDocuments = new HashMap<String,Document>();
    public int num = 0;
    public HashMap<String,Boolean> lM  = new HashMap<String,Boolean>();    
    public String Dir_ant = "";   
    public void init(ServletConfig conf) throws ServletException{
	Element el , el1 ;
	String movie = null;
	
	try{
	    this.dbf = DocumentBuilderFactory.newInstance();
	    this.dbf.setValidating(true);//Valida el DTD
	    this.db = dbf.newDocumentBuilder();
	    db.setErrorHandler(new SimpleErrorHandler());
	}catch(FactoryConfigurationError e){ // ERROR de DocumentBuilderFactory
	    System.err.println("FactoryConfigurationError");
	    System.err.println(e.getMessage());
	}catch(ParserConfigurationException e){ //ERROR de DocumentBuilder
	    System.err.println("ParserConfigurationException");
	    System.err.println(e.getMessage());
	}

	try{
	    synchronized(this.listDocuments){
		busquedaMML();
	    }
	    doc = db.parse("Movies.xml");
	    
	    String tipo = doc.getDoctype().getName();
	    System.out.println("El elemento raíz es: "+ root.getTagName());
	    for (String key : listDocuments.keySet()) {
		System.out.println(key);
		System.out.println(listDocuments.get(key));
	    }
	}catch(IOException e){
	    System.err.println("IOException");
	    System.err.println(e.getMessage());
	}catch(SAXException e){
	    System.err.println("SAXException");
	    System.err.println(e.getMessage());
	}catch(IllegalArgumentException e){
	    System.err.println("IllegalArgumentException");
	    System.err.println(e.getMessage());
	}catch(NullPointerException e){
	    System.err.println("NullPointerException");
	    System.err.println(e.getMessage());
	}
    }
    public void busquedaMML(){
	Boolean acaba = false;
	Element el , el1 = null;
	String movie = null;
	String Test = null;
	
	try{
	    this.doc = this.db.parse(Dir);
	    xPath =  XPathFactory.newInstance().newXPath();
	    NodeList nL0 = (NodeList)xPath.compile("child::Movies/Pelicula/Actor/OtraPelicula/MML").evaluate(doc,XPathConstants.NODESET);
	    NodeList nL = (NodeList)xPath.compile("*").evaluate(doc,XPathConstants.NODESET);
	    el = (Element)nL0.item(0);
	    movie = el.getTextContent();
	    
	    doc.getDocumentElement().normalize();

	    System.out.println("Print Element :" + doc.getDocumentElement().getElementsByTagName("Movies"));
	    System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	    listDocuments.put(movie,doc);
	    // Se imprimen los elementos
	    for(int x = 0 ; x < nL.getLength() ; x++){
		el1 = (Element)nL.item(x);
		System.out.println(el1.getTextContent());
	    }
	    try{
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(movie);
	 	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		nL = el.getElementsByTagName("Movies");
		Node n = nL.item(0);
	    }catch(NullPointerException err){
		acaba = true;
	    }
	    if(acaba){
		System.out.println("No Well-formed");
		lM.put(Dir,false);
		Iterator it = lM.entrySet().iterator();
		System.out.println("[busquedaMML] Despues del error se procede a buscar una nueva URL ");
		while(it.hasNext()){
		    String val = it.next().toString();
		    String aux[] = val.split("=");
		    val = aux[0];
		    System.out.println("Se ha seleccionado "+val);
		    System.out.println("Tiene el valor de "+lM.get(val));
		    if(lM.get(val)){
			Dir = val;
			System.out.println("val : " + val);
			System.out.println("acaba busquedaMML");
		    }
		}
		acaba = false;
	    }
	    listDocuments.put(Dir,doc);
	    for(int x=0 ; x < nL0.getLength() ; x++){
		System.out.println("Vuelta : "+x);
		el = (Element)nL0.item(x);
		
		if(lM.isEmpty()){ //En caso de que este vacio se añade
		    lM.put(el.getTextContent(),true);
		    System.out.println("Se ha añadido con la tabla vacia.");
		    System.out.println("Link a operar "+ el.getTextContent());
		}else{
		    try{ 
			if(!lM.containsKey(el.getTextContent())){ // Si no existe se añade 
			    System.out.println("Se ha añadido un elemento ya que no está vacia");
			    System.out.println("Link a operar "+ el.getTextContent());
			    lM.put(el.getTextContent(),true);
			}
		    }catch(NullPointerException err){ // si da Null se añade
			System.out.println("~~~~~~ NULL ~~~~~~");
			System.out.println("Se ha añadido ya que no existe en la tabla");
			System.out.println("Link a operar "+ el.getTextContent());
			lM.put(el.getTextContent(),true);
		    }
		} 
		if(x == (nL0.getLength()-1)){ //si ya ha visto todo
		    Iterator it = lM.entrySet().iterator();
		    System.out.println("Se procede a buscar una nueva URL " + x);
		    while(it.hasNext()){
			System.out.println("Dentro de la tabla");
			String val = it.next().toString();
			String aux[] = val.split("=");
			val = aux[0];
			System.out.println("Se ha seleccionado "+val);
			System.out.println("Tiene el valor de "+lM.get(val));
			if(lM.get(val)){
			    System.out.println("Ha sido seleccionado para la siguiente interaccion "+ val);
			    Dir = val;
			    System.out.println("val : " + val);
			    lM.put(val,false);
			    if(num < 10){
				busquedaMML();
			    }
			}
			
			System.out.println("Sale de la tabla");
		    }
		}
		
	    }
	}catch(NullPointerException err){
	    err.printStackTrace();
	}catch(IndexOutOfBoundsException err){
	    err.printStackTrace();
        }catch(StackOverflowError err){
	    err.printStackTrace();
        }catch(XPathExpressionException err){
	    err.printStackTrace();
       	}catch(ConcurrentModificationException err){
	    err.printStackTrace();
       	}catch(SAXException err){
	    acaba = true;
	    System.out.println("SAXExeption");
	    System.out.println(err.getMessage());
	}catch(IOException err){
	    System.out.println("IOException");
	    System.out.println(err.getMessage());
	}catch(IllegalArgumentException err){
	    System.out.println("IllegalArgumentException");
	    System.out.println(err.getMessage());
	}
    }
    public String paso = null;
    public String age = null;
    public String film = null;
    public String id = null;
    public String Actor = null;
    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws IOException, ServletException{
	paso = req.getParameter("paso");
	age = req.getParameter("age");
	film = req.getParameter("film");
	id = req.getParameter("id");
	Actor = req.getParameter("Actor");
	res.setContentType("text/html");	
	out = res.getWriter();
	out.println("<html>");
       	out.println("<head>");
	out.println("<link rel='stylesheet' href='mml.css'>");
	out.println("<title>SINT2PR2 - Movies - 2015</title>");
	out.println("</head>");
	out.println("<body>");
	out.println("<TABLE class='contenedor'><TR><TD>");
	int opcion = 0 ;
	//out.println("Opcion paso  = " + paso);
	if(paso == null && opcion == 0){
	    opcion = 3;
	}else{
	    opcion = Integer.parseInt(paso);
	}
	
	switch(opcion){
	case 10 :
	    C1P1seleccionAnio(req,res);
	    break;
	case 11 :
	    C1P2seleccionAnio(req,res);
	    break;
	case 12 :
	    C1P3seleccionAnio(req,res);
	    break;
	case 20 :
	    C2P0seleccionAnio(req,res);
	    break;
	case 21 :
	    C2P1seleccionAnio(req,res);
	    break;
	default :
	    seleccionInicial(req,res);
	    break;
	}
	out.println("</TD></TR></TABLE>");
	out.println("<p class='yo'>Estilo css creado por Jose Antonio González López</p></body>");
	out.println("</html>");
        
    }
    public void seleccionInicial(HttpServletRequest req , HttpServletResponse res)
	throws IOException{
	out.println("<FORM name='form' metod='GET' action=''> ");
	out.println("Seleccione una opción de nuestro catálogo de películas :<br><br>");
	out.println("<input type='radio' name='paso' value='10' checked> Peliculas realizadas en un año determinado.</br>");
	out.println("<input type='radio' name='paso' value='20' > Actor que ha realizado determiadas películas.</br></br>");
	out.println("<input type='submit' name='enviar' value='Siguiente'>");
	out.println("</FORM>");
    }
    public void C1P1seleccionAnio(HttpServletRequest req , HttpServletResponse res)//10
	throws IOException{
	Hashtable<String,Integer> hashLocal = new Hashtable<String,Integer>();
	NodeList nL0 , nL1 = null;
	Element e = null;
	out = res.getWriter();
	try{
	    out.println("<p class = 'seguimiento' > Selección por año </p>");
	    out.println("<FORM name='formp2' metod='GET' action=''>");
	    out.println("Seleccione el año que se ha reproducido la película : ");
	    out.println("<br><br>");
	    for (String key : listDocuments.keySet()) {
		
		//out.println("Pasa por el For " + key);
		doc = listDocuments.get(key);
		nL0 = (NodeList)xPath.compile("child::Movies/Pelicula/Actor/OtraPelicula/Anio").evaluate(doc,XPathConstants.NODESET);
		//out.println("La longitud de este NodeList es de : "+nL0.getLength());
		for(int x = 0 ; x < nL0.getLength() ; x++){
		    e = (Element)nL0.item(x);
		    //out.println("<br><input type='radio' name='age' value='"+ e.getTextContent() +"'> "+e.getTextContent());
		    //out.println("<br>");
		    hashLocal.put(e.getTextContent(),1);
		}
	    }
	    num = 0;
	    Vector v = new Vector(hashLocal.keySet());
	    Collections.sort(v);
	    Iterator it = hashLocal.entrySet().iterator();
	    it = v.iterator();
	    while (it.hasNext()) {
		String key =  (String)it.next();
	  	if(num == 0){
		    out.println("<input type='radio' name='age' value='"+ key  +"' checked> "+key);
		}else{
		    out.println("<input type='radio' name='age' value='"+ key  +"'> "+key);
		}
		out.println("<br>");
		num++;
	    }
	    num = 0;
	}catch(NullPointerException err){
	    err.printStackTrace();
	    out.println("NullPointerException en año : <br>");
	    out.println("If resolver is null.<br>");
	}catch(IndexOutOfBoundsException err){
	    err.printStackTrace();
	    out.println("IndexOutOfBoundsException en año : <br>");
	}catch(XPathExpressionException err){
	    err.printStackTrace();
	    }
	out.println("<br><input type='submit' name='Enviar' value='enviar'>");
	out.println("<input type='hidden' name='paso' value='11'>");	
	out.println("<input type='button' value='Atras' onClick=\"window.location='Movies2';\">");
	out.println("</FORM>");	
    }
    public void C1P2seleccionAnio(HttpServletRequest req , HttpServletResponse res)//11
	throws IOException{
	NodeList nL0 , nL1 = null;
	Element e = null;
	Boolean flag = false;
	String valor  = "";
	out = res.getWriter();
	try{
	    out.println("<p class = 'seguimiento' > Selección por año - "+age+"</p>");
	    out.println("<FORM name='formp2' metod='GET' action=''>");
	    out.println("Seleccione la película deseada : ");
	    for (String key : listDocuments.keySet()) {
		
		//out.println("Pasa por el For " + key);
		doc = listDocuments.get(key);
		nL0 = (NodeList)xPath.compile("/Movies[Anio="+age+"]/Pelicula/Titulo/text()").evaluate(doc,XPathConstants.NODESET);
		nL1 = (NodeList)xPath.compile("/Movies/Pelicula/@ip").evaluate(doc,XPathConstants.NODESET);
		
		num = 0;
		if(nL0.getLength() != 0){
		    for(int x = 0 ; x < nL0.getLength() ; x++){
			flag = true;
			valor = (String) nL0.item(x).getNodeValue();
			//out.println(num);
			//out.println((String)nL1.item(x).getNodeValue());
			if (num == 0){
			    out.println("<br><input type='radio' name='id' value='"+ (String)nL1.item(x).getNodeValue()  +"' checked> "+ valor);
			}else{
			    out.println("<br><input type='radio' name='id' value='"+ (String)nL1.item(x).getNodeValue()  +"'> "+ valor);
			}
			num++;
		    }
		    break;
		}
		
	    }
	    if (!flag){
		out.println("<div class='err' ><br>No se han encontrado peliculas de dicha fecha <br><br></div>");
	    }
	}catch(NullPointerException err){
	    err.printStackTrace();
	    out.println("NullPointerException en año : <br>");
	    out.println("If resolver is null.<br>");
	}catch(IndexOutOfBoundsException err){
	    err.printStackTrace();
	    out.println("IndexOutOfBoundsException en año : <br>");
	}catch(XPathExpressionException err){
	    err.printStackTrace();
	}catch(ClassCastException err){
	    err.printStackTrace();
	    out.println("falla");
	}
	if(flag){
	    out.println("<br><br><input type='submit' name='Enviar' value='enviar'>");
	    out.println("<input type='hidden' name='paso' value='12'>");
	}
	out.println("<input type='hidden' name='age' value='"+age+"'>");
	out.println("<input type='button' value='Inicio' onClick=\"window.location='Movies2';\">");
	out.println("<input type='button' value='Atras' onClick=\"window.location='Movies2?paso=10';\">");
	out.println("</FORM>");	
    }
    public void C1P3seleccionAnio(HttpServletRequest req , HttpServletResponse res)//12
	throws IOException{
	NodeList nL0 , nL1 = null;
	Element e = null;
	Boolean flag = false;
	String valor  = "";
	String enlace = "http";
	out = res.getWriter();
	try{
	    //nL0 = (NodeList)xPath.compile("/Movies/Pelicula[attribute::ip='"+id+"']/*").evaluate(doc,XPathConstants.NODESET);
	    nL0 = (NodeList)xPath.compile("/Movies/Pelicula[attribute::ip='"+id+"']/*").evaluate(doc,XPathConstants.NODESET);
	    nL1 = (NodeList)xPath.compile("/Movies/Pelicula[attribute::ip='"+id+"']/Oscares/*").evaluate(doc,XPathConstants.NODESET);
	    out.println("<p class = 'seguimiento' > Selección por año - "+ age +" - "+ (String) nL0.item(0).getTextContent()+"</p>");
	    out.println("<FORM name='formp2' metod='GET' action=''>");
	    out.println("Aqui tiene la información de la película buscada : ");
	    out.println("<br>");
	    for (String key : listDocuments.keySet()) {
		doc = listDocuments.get(key);
		if(nL0.getLength() != 0){
		    //out.println("<br>La longitud de este NodeList es de : "+nL0.getLength());
		    for(int x = 0 ; x < nL0.getLength() ; x++){
			//out.println("x = "+x+"<br>");
			if(x!=2){
			    flag = true;
			    valor = (String) nL0.item(x).getTextContent();
			    out.println("<br>"+valor);
			}
		    }
		}
		//out.println(nL1.getLength() + "<br>");
		if(nL1.getLength() != 0){
		    out.println("<br><br>Los Oscares recibidos son : <br>");
		    for(int x = 0 ; x < nL1.getLength() ; x++){
			flag = true;
			valor = (String) nL1.item(x).getTextContent();
			out.println("<br>"+valor);
		    }
		}
		
		break;
	    }
	    if (!flag){
		out.println("No se han encontrado informacion de dicha película <br>");
	    }
	}catch(NullPointerException err){
	    err.printStackTrace();
	    out.println("NullPointerException en año : <br>");
	    out.println("If resolver is null.<br>");
	}catch(IndexOutOfBoundsException err){
	    err.printStackTrace();
	    out.println("IndexOutOfBoundsException en año : <br>");
	}catch(XPathExpressionException err){
	    err.printStackTrace();
	}catch(ClassCastException err){
	    err.printStackTrace();
	    out.println("falla");
	}
	out.println("<br><br><input type='button' value='Inicio' onClick=\"window.location='Movies2';\">");
	out.println("<input type='button' value='Atras' onClick=\"window.location='Movies2?paso=11&age="+age+"&enviar=enviar';\">");
	out.println("</FORM>");	
    }

    public void C2P0seleccionAnio(HttpServletRequest req , HttpServletResponse res)//20
	throws IOException{
	Hashtable<String,Integer> hashLocal = new Hashtable<String,Integer>();
	Hashtable<String,Integer> hashAuxLocal = new Hashtable<String,Integer>();
	NodeList nL0 , nL1 = null;
	Element e = null;
	out = res.getWriter();
	try{
	    out.println("<p class = 'seguimiento' > Selección por actor </p>");
	    out.println("<FORM name='formp2' metod='GET' action=''>");
	    out.println("Seleccione el actor : <br>");
	    out.println("<br>");
	    for (String key : listDocuments.keySet()) {
		//out.println("Pasa por el For " + key);
		doc = listDocuments.get(key);
		nL0 = (NodeList)xPath.compile("child::Movies/Pelicula/Actor/Nombre").evaluate(doc,XPathConstants.NODESET);
		//out.println("La longitud de este NodeList es de : "+nL0.getLength());
		for(int x = 0 ; x < nL0.getLength() ; x++){
		    e = (Element)nL0.item(x);
		    hashLocal.put(e.getTextContent(),1);
		    //String valor =ap1Nom[1]+" "+ap1Nom[0];
		    //out.println("<br><input type='radio' name='age' value='"+ e.getTextContent() +"'> "+e.getTextContent());
		    //out.println("<br>");
		}
	    }
	    num = 0;




	    
	    
	    Vector v = new Vector(hashLocal.keySet());
	    Collections.sort(v);
	    Iterator it = hashLocal.entrySet().iterator();
	    it = v.iterator();
	    while (it.hasNext()) {
		String key =  (String)it.next();
		String aux = "";
		String[] ap1Nom = key.split(" ");
		
		if(ap1Nom.length == 2){
		    //out.println("<br>0"+ap1Nom[0]);
		    //out.println("<br>1"+ap1Nom[1]);
		    aux = ap1Nom[1]+" , "+ap1Nom[0];
		}else{
		    aux = key;
		}
		if(num == 0){
		    out.println("<input type='radio' name='Actor' value='"+ key  +"' checked> "+aux);
		}else{
		    out.println("<input type='radio' name='Actor' value='"+ key  +"'> "+aux);
		}
		out.println("<br>");
		num++;
	    }
	    num = 0;
	}catch(NullPointerException err){
	    err.printStackTrace();
	    out.println("NullPointerException en año : <br>");
	    out.println("If resolver is null.<br>");
	}catch(IndexOutOfBoundsException err){
	    err.printStackTrace();
	    out.println("IndexOutOfBoundsException en año : <br>");
	}catch(XPathExpressionException err){
	    err.printStackTrace();
	    }
	out.println("<br><br><input type='submit' name='Enviar' value='enviar'>");
	out.println("<input type='hidden' name='paso' value='21'>");	
	out.println("<input type='button' value='Atras' onClick=\"window.location='Movies2';\">");
	out.println("</FORM>");	
    }
    public void C2P1seleccionAnio(HttpServletRequest req , HttpServletResponse res)//21
	throws IOException{
	Hashtable<String,Integer> hashLocal = new Hashtable<String,Integer>();
	NodeList nL0 , nL1 = null;
	Element e = null;
	String enlace = "http";
	out = res.getWriter();
	try{
	    out.println("<p class = 'seguimiento' > Selección por año - "+ Actor +"</p><br>");
	    out.println("<FORM name='formp2' metod='GET' action=''>");
	    out.println("A continuación se le muestra la informacion de " +Actor +" : ");
	    out.println("<br>");
	    
	    for (String key : listDocuments.keySet()) {
		//out.println("Pasa por el For " + key);
		doc = listDocuments.get(key);
		//out.println("child::Movies/Pelicula/Actor[Nombre='"+Actor+"']/text()");
		nL0 = (NodeList)xPath.compile("child::Movies/Pelicula/Actor[Nombre='"+Actor+"']/text()").evaluate(doc,XPathConstants.NODESET);
		
		//out.println("La longitud de este NodeList es de : "+ nL0.getLength());
		if(nL0.getLength() != 0){
		    for(int x = 0 ; x < nL0.getLength() ; x++){
			String valor = (String) nL0.item(x).getTextContent();
			if(!valor.substring(0,3).equals(enlace)){
			    if(!valor.equals(" ")){
				out.println((String)nL0.item(x).getNodeValue());
				//out.println(valor+"<br>");
			    }
			}
			
		    }
		    break;
		}
	    }
	    
	    out.println("<br> <br>A participado en :<br>");
	    String aux ="";
	    for (String key : listDocuments.keySet()){
		doc = listDocuments.get(key);
		//out.println("<br>//Pelicula[Actor/Nombre='"+Actor+"']/Titulo<br>");
		nL0 = (NodeList)xPath.compile("//Pelicula[Actor/Nombre='"+Actor+"']/Titulo").evaluate(doc,XPathConstants.NODESET);
		nL1 = (NodeList)xPath.compile("//Pelicula[Actor/Nombre='"+Actor+"']/Actor/@papel").evaluate(doc,XPathConstants.NODESET);
		//out.println("<br>Longitud nL0 "+nL0.getLength());
		
		if(nL0.getLength() != 0){
		   for(int x = 0 ; x < nL0.getLength() ; x++){
		       if(!hashLocal.containsKey((String)nL0.item(x).getTextContent())){
			   hashLocal.put((String)nL0.item(x).getTextContent(),1);
			   out.println("<div class='tit'><br> En la película con titulo : "+(String)nL0.item(x).getTextContent()+"</div>");
			   out.println("<br> Ha participado como actor "+(String)nL1.item(x).getTextContent());
			   out.println("<br>");
		       }
		   } 
		}
	    }
	
	}catch(NullPointerException err){
	    err.printStackTrace();
	    out.println("NullPointerException en año : <br>");
	    out.println("If resolver is null.<br>");
	}catch(IndexOutOfBoundsException err){
	    err.printStackTrace();
	    out.println("IndexOutOfBoundsException en año : <br>");
	}catch(XPathExpressionException err){
	    err.printStackTrace();
	    }
	out.println("<br><br><input type='button' value='Inicio' onClick=\"window.location='Movies2';\">");
	out.println("<input type='button' value='Atras' onClick=\"window.location='Movies2?paso=20';\">");
	out.println("</FORM>");	
    }
}
