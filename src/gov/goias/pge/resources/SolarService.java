package gov.goias.pge.resources;

import java.io.File;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import com.lowagie.text.pdf.PdfReader;

@Path("/solarservice")
public class SolarService {
	
	@Path("/delete/{core}/{id}")
	@GET
	@Produces("application/json")
	public void deleteIndex(@PathParam("core") String core, @PathParam("id") String id) throws Exception{
		try{
			String urlString = "http://10.6.56.150:8983/solr/PGE_CEJUR/";
			SolrClient solr = new HttpSolrClient.Builder(urlString).build();
			solr.deleteById("/mnt/disco02/CEJUR/"+id);
			solr.commit();
			solr.close();
		}catch(Exception e ){
			throw new Exception("Erro ao deletar index -> "+e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Path("/index/{core}/{nomeArquivo}")
	@GET
	@Produces("application/json")
	public void indexFile(@PathParam("core") String core, @PathParam("nomeArquivo") String nomeArquivo) throws Exception{
		try{
			String urlString = "http://10.6.56.150:8983/solr/PGE_CEJUR/";

			File file = null;
			file = new File("/mnt/CEJUR/" + nomeArquivo);
			if(!file.exists()){
				System.out.println("ARQUIVO "+nomeArquivo+" NÃO ENCONTRADO.");
				throw new Exception("Arquivo não encontrado.");
			}
			
			PdfReader reader = new PdfReader(file.getAbsolutePath());
			HashMap<String, String> info = reader.getInfo();
	
			SolrClient solr = new HttpSolrClient.Builder(urlString).build();
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", "/mnt/disco02/CEJUR/"+nomeArquivo);
			document.addField("name", nomeArquivo);
			document.addField("titulo",  info.get("Title"));
			document.addField("assunto",  info.get("Subject"));
			document.addField("autor",  info.get("Author"));
			document.addField("palavrachave",  info.get("Keywords"));
			document.addField("uploader",  info.get("Uploader"));
			solr.add(document);
			solr.commit();
			solr.close();
		}catch(Exception e ){
			throw new Exception("Erro ao index arquivo -> "+e.getMessage());
		}
	}
}
