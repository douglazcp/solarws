package gov.goias.pge.resources;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.util.NamedList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.File;

@Path("/solarservice")
public class SolarService {

	public enum Codigo {
		SUCESSO, ERRO
	}

	@Path("/")
	@GET
	@Produces("application/json")
	public String index(){
		return montaRespostaJson(Codigo.SUCESSO.toString(), "index","");
	}

	@Path("/delete/{core}/{id}")
	@GET
	@Produces("application/json")
	public String deleteIndex(@PathParam("core") String core, @PathParam("id") String id){
		try {
			String urlString = "http://10.6.56.150:8983/solr/"+core+"/";
			SolrClient solr = new HttpSolrClient.Builder(urlString).build();
			id = id.replaceAll("_", " ");
			solr.deleteById(id);
			solr.commit();
			solr.close();
			return montaRespostaJson(Codigo.SUCESSO.toString(),"deleteIndex("+core+","+id+")","");
		} catch (Exception e) {
			return montaRespostaJson(Codigo.ERRO.toString(),"deleteIndex("+core+","+id+")",e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Path("/indexFile/{core}/{nomeArquivo}")
	@GET
	@Produces("application/json")
	public String indexFile(@PathParam("core") String core, @PathParam("nomeArquivo") String nomeArquivo){
		try {
			String urlString = "http://10.6.56.150:8983/solr/"+core+"/";
			nomeArquivo = nomeArquivo.replace("+", " ");
			File file = new File(nomeArquivo);
			if (!file.exists()) {
				throw new Exception("Arquivo "+nomeArquivo+" não encontrado");
			}
			SolrClient client = new HttpSolrClient.Builder(urlString).build();
			ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");
			req.addFile(file, "");
			req.setParam("extractOnly", "false");
			NamedList<Object> result = client.request(req);
			client.commit();
			client.close();
			return montaRespostaJson(Codigo.SUCESSO.toString(),"indexFile("+core+","+nomeArquivo+")","");
		} catch (Exception e) {
			return montaRespostaJson(Codigo.ERRO.toString(),"indexFile("+core+","+nomeArquivo+")",e.getMessage());
		}
	}

	private String montaRespostaJson(String codigo, String metodo, String descricao){
		String json = "";
		json+= "{ ";
		json+= "	\"resultado\":\""+codigo+"\", ";
		json+= "	\"metodo\": \""+metodo+"\", ";
		json+= "	\"descricao\":\""+descricao+"\" ";
		json+= "} ";
		return json;
	}
}
