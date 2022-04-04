package pkg;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.lang.System;

public class Dollar {
	
	double buying_usd,selling_usd;
	
	String exchangeDate;
	

	public String getExchangeDate() {
		return exchangeDate;
	}


	public void setExchangeDate(String exchangeDate) {
		this.exchangeDate = exchangeDate;
	}


	public double getBuying_usd() {
		return buying_usd;
	}


	public void setBuying_usd(double buying_usd) {
		this.buying_usd = buying_usd;
	}


	public double getSelling_usd() {
		return selling_usd;
	}


	public void setSelling_usd(double selling_usd) {
		this.selling_usd = selling_usd;
	}


	public void parseNRB() {
		 try {


	            //String sellUSD="",buyUSD="";

	            //url of site
	            String page = "https://www.nrb.org.np";

	            // Connection conn = Jsoup.connect(page);
	            // Document doc = conn.get();
	            //connecting and converting html to Document object
	            Document doc = Jsoup.connect(page).get();
	            

	            // title of the page
	            //System.out.println("title of the page "+doc.title());

	            //Getting element by id (which is unique in a page)
	            /*
	             * The page has the section tag and currencies are with in the section tag with id
	             * content
	             */
	            Element sectionId = doc.getElementById("content");
	            
	            Elements exDate = doc.getElementsByClass("card-bt-header-subtitle");
	            
	            setExchangeDate(exDate.get(1).text());
	            
	            System.out.println(getExchangeDate().substring(15).trim());
	            
	            

	            /*
	             * Web site is copy pasted. So there are two identical tables with content
	             * different only. Currency is in the first table. Get the content of first table.
	             * Element class is used to extract single table. Use Elements class in case of using both tables.
	             * Extracted based on table tag.
	             */
	             Element currencyTableOne = sectionId.getElementsByTag("table").first();
	             
	             

	                /*
	                 * From the table, currency details are in body of the table defined as tbody
	                 * only. Additional attributes undefined.
	                 */
	             Elements currencyBody = currencyTableOne.getElementsByTag("tbody");

	                /* Now iterate over table body and check the available currencies */

	             for(Element eachCurrencyRow : currencyBody) {
	                    /*
	                     * Below SOUT shows the available currencies in text format. Remove text method
	                     * to view in html format.
	                     */
	                 //System.out.println("Exchange Rates are "+ eachCurrencyRow.text());
	                    /* Again parse over each row of currency ie tr tag */
	                 for(Element currency: eachCurrencyRow.select(("tr"))) {
	                        /* Below SOUT shows the currency after iteration, so break after first iteration to load USD only.
	                         * Use Iterate<Element> in next phase for better performance and extracting first row */
	                     //System.out.println("Currency Rate is  "+ currency.text());

	                        /* Again parse through td tag */
	                     Elements currencyData = currency.select("td");
	                     //System.out.println("Content of Row is "+ currencyData);
	                     if(currencyData.size() > 1) {
	                    	 setBuying_usd(Double.parseDouble(currencyData.get(1).text()));
	                         //buyUSD = currencyData.get(1).text();
	                         //System.out.println("buying price of USD is "+ buyUSD);
	                    	 setSelling_usd(Double.parseDouble(currencyData.get(2).text()));
	                         //sellUSD = currencyData.get(2).text();
	                         //System.out.println("selling price of USD is "+ sellUSD);
	                     }
	                        /*
	                         * //As the USD is first element, break the loop after first execution.
	                         * What if the USD is shifted to second element?
	                         */
	                     break;
	                 }

	             }

	             //System.out.println("Buying USD is "+buyUSD);
	             //System.out.println("Selling USD is "+ sellUSD);




	        } catch (IOException ex) {

	            /*
	             * Handle exception 1. If local internet is down. 2. If NRB site is down. 3. If
	             * NRB domain and internet related have problems. 4. If the site is upgraded or
	             * modified. 5. If USD value is unpopulated due to error in NRB site/application.
	             */
	            System.out.printf("Not connected to internet ", ex.getMessage());
	        }


	}

}
