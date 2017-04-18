import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;

import com.itextpdf.text.pdf.PdfReader;

import gov.goias.pge.resources.PDFLayoutTextStripper;

public class Main {
	public static void main(String[] args) throws Exception{
		/*
		SolrClient client = new HttpSolrClient.Builder("http://10.6.56.150:8983/solr/PGE_CEJUR").build();
		ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");
		File file = null;
		file = new File("C:\\Users\\douglas-cp\\Documents\\Javadesenv\\intellij\\configs_sicop\\solr\\Sem.pdf");
		req.addFile(file, "");
		req.setParam("extractOnly", "false");
		NamedList<Object> result = client.request(req);
		client.commit();
		client.close();
		*/
		deleteIndex("", "/mnt/disco02/CEJUR/inicial - acao civil publica cc improbidade administrativa - pro saude - lrs - assinado.pdf");
	}
	
	public static void indexFile() throws Exception{
		try{
			String core = "PGE_CEJUR";
			String nomeArquivo = "arquivo upload.pdf";
			String urlString = "http://10.6.56.150:8983/solr/"+core+"/";
			nomeArquivo = nomeArquivo.replace("+", " ");
			File file = null;
			file = new File("C:\\Users\\douglas-cp\\Documents\\Javadesenv\\intellij\\configs_sicop\\solr\\" + nomeArquivo);
			if(!file.exists()){
				throw new Exception("Arquivo não encontrado.");
			}
			
			PdfReader reader = new PdfReader(file.getAbsolutePath());
			HashMap<String, String> info = reader.getInfo();
	
			SolrClient solr = new HttpSolrClient.Builder(urlString).build();
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", "C:\\Users\\douglas-cp\\Documents\\Javadesenv\\intellij\\configs_sicop\\solr\\"+nomeArquivo);
			document.addField("name", nomeArquivo);
			document.addField("title",  info.get("Title"));
			document.addField("subject",  info.get("Subject"));
			document.addField("author",  info.get("Author"));
			document.addField("keywords",  info.get("Keywords"));
			document.addField("uploader",  info.get("Uploader"));
			document.addField("created",  info.get("Created"));
			document.addField("stream_size",  info.get("stream_size"));
			document.addField("fullText", "");
			
	        try {
	        	StringBuilder string = new StringBuilder();
	            PDFParser pdfParser = new PDFParser(new FileInputStream("C:\\Users\\douglas-cp\\Documents\\Javadesenv\\intellij\\configs_sicop\\solr\\arquivo upload.pdf"));
	            pdfParser.parse();
	            PDDocument pdDocument = new PDDocument(pdfParser.getDocument());
	            PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper();
	            string.append(pdfTextStripper.getText(pdDocument));
	            document.addField("content", string);
	            document.addField("stored", true);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			solr.add(document);
			solr.commit();
			solr.close();
		}catch(Exception e ){
			throw new Exception("Erro ao index arquivo -> "+e.getMessage());
		} 
	}
	
	public static void deleteIndex(String core, String id) throws Exception{
		try{
			String urlString = "http://10.6.56.150:8983/solr/PGE_CEJUR/";
			SolrClient solr = new HttpSolrClient.Builder(urlString).build();
			solr.deleteById(id);
			solr.commit();
			solr.close();
		}catch(Exception e ){
			throw new Exception("Erro ao deletar index -> "+e.getMessage());
		}
	}
}