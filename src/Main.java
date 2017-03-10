import java.io.File;
import java.util.HashMap;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import com.lowagie.text.pdf.PdfReader;

public class Main {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception{
		try{
			String urlString = "http://10.6.56.150:8983/solr/PGE_CEJUR/";
			String nomeArquivo = "acordo_ortografico.pdf";
			
			File file = null;
			file = new File("C:\\Users\\douglas-cp\\Documents\\Javadesenv\\intellij\\configs_sicop\\solr\\" + nomeArquivo);
			if(!file.exists()){
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
