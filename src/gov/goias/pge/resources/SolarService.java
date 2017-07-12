package gov.goias.pge.resources;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.util.NamedList;

@Path("/solarservice")
public class SolarService {

	@Path("/delete/{core}/{id}")
	@GET
	@Produces("application/json")
	public void deleteIndex(@PathParam("core") String core, @PathParam("id") String id) throws Exception {
		try {
			String urlString = "http://10.6.56.150:8983/solr/PGE_CEJUR/";
			SolrClient solr = new HttpSolrClient.Builder(urlString).build();
			id = id.replaceAll("_", " ");
			solr.deleteById("/mnt/disco02/CEJUR/" + id);
			solr.commit();
			solr.close();
		} catch (Exception e) {
			throw new Exception("Erro ao deletar index -> " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Path("/index/{core}/{nomeArquivo}")
	@GET
	@Produces("application/json")
	public void indexFile(@PathParam("core") String core, @PathParam("nomeArquivo") String nomeArquivo) throws Exception {
		try {
			String urlString = "http://10.6.56.150:8983/solr/" + core + "/";
			nomeArquivo = nomeArquivo.replace("+", " ");
			File file = null;
			file = new File("/mnt/disco02/CEJUR/" + nomeArquivo);
			// file = new
			// File("C:\\Users\\douglas-cp\\Documents\\Javadesenv\\intellij\\configs_sicop\\solr\\"
			// + nomeArquivo);
			if (!file.exists()) {
				throw new Exception("Arquivo não encontrado.");
			}

			SolrClient client = new HttpSolrClient.Builder(urlString).build();
			ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");
			req.addFile(file, "");
			req.setParam("extractOnly", "false");
			NamedList<Object> result = client.request(req);
			client.commit();
			client.close();
		} catch (Exception e) {
			throw new Exception("Erro ao index arquivo -> " + e.getMessage());
		}
	}
}
