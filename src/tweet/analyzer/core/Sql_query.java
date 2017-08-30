package tweet.analyzer.core;
import javax.swing.JOptionPane;


public class Sql_query {
	public static void query(int giorno, int mese, int anno, int ora, int intervallo, int numeroDiOre, String keyword)
	{
		
		//PER INTERVALLO SI INTENDE INTERVALLO DI MINUTI
		double frazioniDiOra=60/intervallo;	
		while(60%intervallo!=0)
		{
			//INSERIRE UN ALTRO INTERVALLO_IN_MINUTI CHE SIA SOTTOMULTIPLO DI 60 
			intervallo=(Integer.parseInt(JOptionPane.showInputDialog("Inserire un sottomultiplo di 60 come intervallo di minuti:")));
			frazioniDiOra=60/intervallo;
		}
		
		
		int frazioniDiEvento = (int)(frazioniDiOra)*numeroDiOre;	
		//  ^^^^^^^^^^^^^^^^QUESTO VALORE INDICA IL NUMERO DI QUERIES DA STAMPARE
		int frazioniDiOraInt = (int) (frazioniDiOra);
		// 5 E' IL NUMERO DI ORE IN UNA SERATA
		
		String [] minuti = new String[60]; minuti[0]="00";
		for(int k = 1;k<60;k++)
			if(k<=9)
				minuti[k]="0"+k;
			else
				minuti[k]=""+k;
		int i = 0;		//	SERVE A FAR GIRARE L'INDICE SULL'ARRAY MINUTI
						//	VA AZZERATO ALLA FINE DI OGNI ORA
						//	UN'ORA DURA 60/INTERVALLO_IN_MINUTI
		
		
		int conta_query = 0;	//CONTA QUANTE QUERIES SONO STATE SCRITTE
								//SERVE A FAR SCATTARE DATA/ORA/MINUTI
		
		boolean cambio_data=false;	//EVITA ENTRATE NON RICHIESTE NELL'IF CHE MODIFICA L'ORA DA 24 A 0

		
		for(int k = 0;k<frazioniDiEvento;k++)
		{
			
			if(conta_query>0) stampa("union all");
			//L'ORA SCATTA AD OGNI MULTIPLO DI FRAZIONIDIORA
			if((conta_query%frazioniDiOraInt)==0 && conta_query!=0)
			{
				ora++; 
				i=0;
			}
			if(ora==24 && cambio_data==false)
			{
				ora=0;										//REGOLARE									//BISESTILE
				if(giorno>=31 || (giorno==28  && mese==2 && (anno%4)!=0) || (giorno==29 && mese==2 && (anno%4)==0) || (giorno>=30 && (mese==4 ||  mese==6 || mese == 9 || mese ==11 )))
				{
					if(mese==12)
					{
						anno++;
						mese=1;
					}
					else
						mese++;
					giorno=1;
				}
				else
					giorno++;
				cambio_data=true;
			}
			
			if(ora<10)
			{
				
				if(mese>=10 && giorno>=10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-"+mese+"-"+giorno+" 0"+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-"+mese+"-"+giorno+" 0"+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '%"+keyword+"%')");
				}
				
				if(mese<10 && giorno>=10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-0"+mese+"-"+giorno+" 0"+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-0"+mese+"-"+giorno+" 0"+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '%"+keyword+"%')");
				}
				
				if(mese<10 && giorno<10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-0"+mese+"-0"+giorno+" 0"+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-0"+mese+"-0"+giorno+" 0"+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '%"+keyword+"%')");
					
				}
				
				if(mese>=10 && giorno<10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-"+mese+"-0"+giorno+" 0"+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-"+mese+"-0"+giorno+" 0"+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '%"+keyword+"%')");
					
				}
				
				conta_query++;
				i = i+intervallo;
			}
			else
			{
				
				if(mese>=10 && giorno>=10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-"+mese+"-"+giorno+" "+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-"+mese+"-"+giorno+" "+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '%"+keyword+"%')");
				}
				
				if(mese<10 && giorno>=10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-0"+mese+"-"+giorno+" "+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-0"+mese+"-"+giorno+" "+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '%"+keyword+"%')");
				}
				
				if(mese<10 && giorno<10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-0"+mese+"-0"+giorno+" "+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-0"+mese+"-0"+giorno+" "+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '%"+keyword+"%')");
					
				}
				
				if(mese>=10 && giorno<10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-"+mese+"-0"+giorno+" "+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-"+mese+"-0"+giorno+" "+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '%"+keyword+"%')");
					
				}
				conta_query++;
				i = i+intervallo;
			}
			
			
		}
		

	}
	
	
	
	
	
	
	
	
	public static void query2(int giorno, int mese, int anno, int ora, int intervallo, int numeroDiOre, String keyword)
	{
		
		//PER INTERVALLO SI INTENDE INTERVALLO DI MINUTI
		double frazioniDiOra=60/intervallo;	
		while(60%intervallo!=0)
		{
			//INSERIRE UN ALTRO INTERVALLO_IN_MINUTI CHE SIA SOTTOMULTIPLO DI 60 
			intervallo=(Integer.parseInt(JOptionPane.showInputDialog("Inserire un sottomultiplo di 60 come intervallo di minuti:")));
			frazioniDiOra=60/intervallo;
		}
		
		
		int frazioniDiEvento = (int)(frazioniDiOra)*numeroDiOre;	
		//  ^^^^^^^^^^^^^^^^QUESTO VALORE INDICA IL NUMERO DI QUERIES DA STAMPARE
		int frazioniDiOraInt = (int) (frazioniDiOra);
		// 5 E' IL NUMERO DI ORE IN UNA SERATA
		
		String [] minuti = new String[60]; minuti[0]="00";
		for(int k = 1;k<60;k++)
			if(k<=9)
				minuti[k]="0"+k;
			else
				minuti[k]=""+k;
		int i = 0;		//	SERVE A FAR GIRARE L'INDICE SULL'ARRAY MINUTI
						//	VA AZZERATO ALLA FINE DI OGNI ORA
						//	UN'ORA DURA 60/INTERVALLO_IN_MINUTI
		
		
		int conta_query = 0;	//CONTA QUANTE QUERIES SONO STATE SCRITTE
								//SERVE A FAR SCATTARE DATA/ORA/MINUTI
		
		boolean cambio_data=false;	//EVITA ENTRATE NON RICHIESTE NELL'IF CHE MODIFICA L'ORA DA 24 A 0

		
		for(int k = 0;k<frazioniDiEvento;k++)
		{
			
			if(conta_query>0) stampa("union all");
			//L'ORA SCATTA AD OGNI MULTIPLO DI FRAZIONIDIORA
			if((conta_query%frazioniDiOraInt)==0 && conta_query!=0)
			{
				ora++; 
				i=0;
			}
			if(ora==24 && cambio_data==false)
			{
				ora=0;										//REGOLARE									//BISESTILE
				if(giorno>=31 || (giorno==28  && mese==2 && (anno%4)!=0) || (giorno==29 && mese==2 && (anno%4)==0) || (giorno>=30 && (mese==4 ||  mese==6 || mese == 9 || mese ==11 )))
				{
					if(mese==12)
					{
						anno++;
						mese=1;
					}
					else
						mese++;
					giorno=1;
				}
				else
					giorno++;
				cambio_data=true;
			}
			
			if(ora<10)
			{
				
				if(mese>=10 && giorno>=10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-"+mese+"-"+giorno+" 0"+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-"+mese+"-"+giorno+" 0"+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '"+keyword+"')");
				}
				
				if(mese<10 && giorno>=10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-0"+mese+"-"+giorno+" 0"+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-0"+mese+"-"+giorno+" 0"+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '"+keyword+"')");
				}
				
				if(mese<10 && giorno<10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-0"+mese+"-0"+giorno+" 0"+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-0"+mese+"-0"+giorno+" 0"+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '"+keyword+"')");
					
				}
				
				if(mese>=10 && giorno<10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-"+mese+"-0"+giorno+" 0"+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-"+mese+"-0"+giorno+" 0"+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '"+keyword+"')");
					
				}
				
				conta_query++;
				i = i+intervallo;
			}
			else
			{
				
				if(mese>=10 && giorno>=10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-"+mese+"-"+giorno+" "+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-"+mese+"-"+giorno+" "+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '"+keyword+"')");
				}
				
				if(mese<10 && giorno>=10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-0"+mese+"-"+giorno+" "+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-0"+mese+"-"+giorno+" "+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '"+keyword+"')");
				}
				
				if(mese<10 && giorno<10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-0"+mese+"-0"+giorno+" "+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-0"+mese+"-0"+giorno+" "+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '"+keyword+"')");
					
				}
				
				if(mese>=10 && giorno<10)
				{
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 8");
					stampa("and CREATION_DATE>'"+anno+"-"+mese+"-0"+giorno+" "+ora+":"+minuti[i]+":00.000'");
					stampa("and CREATION_DATE<'"+anno+"-"+mese+"-0"+giorno+" "+ora+":"+minuti[i+intervallo-1]+":59.999'");
					stampa("and text like '"+keyword+"')");
					
				}
				conta_query++;
				i = i+intervallo;
			}
			
			
		}
		

	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void stampa(char stringa)
	{
		System.out.println(stringa);
	}
	
	public static void stampa(int stringa)
	{
		System.out.println(stringa);
	}
	
	public static void stampa(double stringa)
	{
		System.out.println(stringa);
	}
	
	public static void stampa(long stringa)
	{
		System.out.println(stringa);
	}
	
	public static void stampa(boolean stringa)
	{
		System.out.println(stringa);
	}
	
	public static void stampa(String stringa)
	{
		System.out.println(stringa);
	}
	
	public static boolean controlloData(String data)
	{
		//QUESTO METODO NON TERMINA FINCHE' LA DATA NON E' SCRITTA NEL FORMATO CORRETTO
		boolean controllo;
		for(int i = 0; i<data.length(); i++)
		{
			
			while(data.length()!=10)
				data = JOptionPane.showInputDialog("Errore nell'inserimento della data. Inserire nel formato AAAA-MM-GG:");
			while(data.charAt(4)!='-' || data.charAt(7)!='-')
				data = JOptionPane.showInputDialog("Errore nell'inserimento della data. Inserire nel formato AAAA-MM-GG:");
			if(i!=4 && i!=7 && data.charAt(i)!= '0' && data.charAt(i)!= '1' && data.charAt(i)!= '2' && data.charAt(i)!= '3' && data.charAt(i)!= '4' && data.charAt(i)!= '5' && data.charAt(i)!= '6' && data.charAt(i)!= '7' && data.charAt(i)!= '8' && data.charAt(i)!= '9')	
			{	
				data = JOptionPane.showInputDialog("Errore nell'inserimento della data. Inserire nel formato AAAA-MM-GG:");
				i=0;
			}
		}
		
		controllo=true;
		return controllo;

	}
	
	
	public static int getAnno(String data)
	{
		int a = data.charAt(0), b=data.charAt(1), c=data.charAt(2), d=data.charAt(3);
		int a_numeric=Character.getNumericValue(a);
		int b_numeric=Character.getNumericValue(b);
		int c_numeric=Character.getNumericValue(c);
		int d_numeric=Character.getNumericValue(d);
		String data_year1=""+a_numeric+b_numeric+c_numeric+d_numeric;
		int data_year=Integer.parseInt(data_year1);
		return data_year;
	}
	
	
	public static int getMese(String data)
	{
		int a = data.charAt(5), b=data.charAt(6);
		int a_numeric=Character.getNumericValue(a);
		int b_numeric=Character.getNumericValue(b);
		String data_month1=""+a_numeric+b_numeric;
		int data_month=Integer.parseInt(data_month1);
		return data_month;
	}
	
	
	public static int getGiorno(String data)
	{
		int a = data.charAt(8), b=data.charAt(9);
		int a_numeric=Character.getNumericValue(a);
		int b_numeric=Character.getNumericValue(b);
		String data_day1=""+a_numeric+b_numeric;
		int data_day=Integer.parseInt(data_day1);
		return data_day;
	}
	
	
	
	
	
	
	
	public static void main(String []args)
	{
		
		//INSERIMENTO DA FINESTRA JOPTIONPANE
		/*
		String keyword = JOptionPane.showInputDialog("Inserisci keyword:");
		int ora = Integer.parseInt(JOptionPane.showInputDialog("Inserire orario di inizio:"));
		String data = JOptionPane.showInputDialog("Inserire data di inizio (AAAA-MM-GG):");
		int intervallo = Integer.parseInt(JOptionPane.showInputDialog("Inserire intervallo di minuti:"));
		int numeroDiOre= Integer.parseInt(JOptionPane.showInputDialog("Inserire numero di ore:"));
		*/
		
		//INSERIMENTO DA CODICE
		
		String keyword = "bova";			//INSERIRE KEYWORD QUI
		int ora = 21;						//INSERIRE ORARIO DI INIZIO RUN QUI
		String data = "2016-04-01";			//INSERIRE DATA DI INIZIO RUN QUI
		int intervallo = 1;					//INSERIRE INTERVALLO DI MINUTI QUI
		int numeroDiOre=2;					//INSERIRE NUMERO DI ORE QUI
		
		
		
		controlloData(data);				//SE IL PROGRAMMA SUPERA QUESTO CONTROLLO LA DATA E' CORRETTA

		query(getGiorno(data), getMese(data), getAnno(data), ora, intervallo, numeroDiOre, keyword);
				
			
		
	}
}
