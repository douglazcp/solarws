package gov.goias.pge.resources;

import Util.DataUtil;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.File;
import java.util.Date;

@Path("/solarservice")
public class SolarService {

	public enum Codigo {
		SUCESSO, ERRO
	}

	public enum CORE{
		PGE_PROV_HOMO, PGE_PROV_PROD, PGE_CEJUR
	}

	@Path("/")
	@GET
	@Produces("application/json")
	public String index(){
		String json = "";

        json += " {";
        json += "     \"metodos\": {";
        json += "         \"deleteIndex\": {";
        json += "             \"parametros\":\"core,id\"";
        json += "         },";
        json += "         \"indexFile\": {";
        json += "             \"parametros\":\"core,nomeArquivo\"";
        json += "         }";
        json += "     },";
        json += "     \"retorno\":{";
        json += "     \"resultado\": \"SUCESSO/ERRO\",";
        json += "             \"metodo\":\"METODO INVOCADO\",";
        json += "             \"descricao\": \"ERRO\"";
        json += "     },";
        json += " \"versao\":\"1.0\" ";
        json += " }";

		return json;
	}

	@SuppressWarnings("unchecked")
	@Path("/indexFile/{core}/{nomeArquivo}")
	@GET
	@Produces("application/json")
	public String indexFile(@PathParam("core") String core, @PathParam("nomeArquivo") String nomeArquivo){
		try {
			String urlString = "http://10.6.56.150:8983/solr/"+core+"/";
			nomeArquivo = nomeArquivo.replace("+", " ");
			//file = new File("/mnt/disco02/CEJUR/" + nomeArquivo);
			File file = new File(montaCaminhoFisicoArquivo(nomeArquivo, core));
			if (!file.exists()) {
				throw new Exception("Arquivo "+nomeArquivo+" não encontrado");
			}
			SolrClient client = new HttpSolrClient.Builder(urlString).build();
			ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");
			req.addFile(file, "");
			req.setParam("extractOnly", "false");
			client.request(req);
			client.commit();
			client.close();
			return montaRespostaJson(Codigo.SUCESSO.toString(),"indexFile("+core+","+nomeArquivo+")","");
		} catch (Exception e) {
			return montaRespostaJson(Codigo.ERRO.toString(),"indexFile("+core+","+nomeArquivo+")",e.getMessage());
		}
	}

	@Path("/delete/{core}/{id}")
	@GET
	@Produces("application/json")
	public String deleteIndex(@PathParam("core") String core, @PathParam("id") String id){
		try {
			String urlString = "http://10.6.56.150:8983/solr/"+core+"/";
			SolrClient solr = new HttpSolrClient.Builder(urlString).build();
			id = montaCaminhoFisicoArquivo(id.replaceAll("_", " "), core);
			solr.deleteById(id);
			solr.commit();
			solr.close();
			return montaRespostaJson(Codigo.SUCESSO.toString(),"deleteIndex("+core+","+id+")","");
		} catch (Exception e) {
			return montaRespostaJson(Codigo.ERRO.toString(),"deleteIndex("+core+","+id+")",e.getMessage());
		}
	}

	private String montaCaminhoFisicoArquivo(String arquivo, String core){
		if(core.equals(CORE.PGE_PROV_HOMO.toString())){
			return "/mnt/disco02/providencias/homo/" + DataUtil.formatar(new Date(), "YYYY")+"/"+ DataUtil.formatar(new Date(), "MM")+"/"+ arquivo;
		}else if(core.equals(CORE.PGE_PROV_PROD.toString())){
			return "/mnt/disco02/providencias/prod/" + DataUtil.formatar(new Date(), "YYYY")+"/"+ DataUtil.formatar(new Date(), "MM")+"/"+ arquivo;
		}else{ //PGE_CEJUR
			return "/mnt/disco02/CEJUR/" + arquivo;
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
