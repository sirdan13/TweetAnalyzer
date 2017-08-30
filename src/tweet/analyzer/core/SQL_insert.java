package tweet.analyzer.core;

import javax.swing.JOptionPane;


public class SQL_insert {

	public static void query(String artista, int data) {
		int frazioni = 10;
		int ora=21, minuti2=29, minuti3=59, conta=0;
		String minuti="00";
		stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 7");
		stampa("and CREATION_DATE>'2016-02-"+data+" "+ora+":00:00.000'");
		stampa("and CREATION_DATE<'2016-02-"+data+" "+ora+":29:59.999'");
		stampa("and text like '%"+artista+"%')");
		conta++;
		for(int i = 0;i<frazioni;i++)
		{
			if((conta%2)==0)
			{
				ora=ora+1;
				if(conta>=6)
				{
					if(conta==6) data=data+1;
					if(conta==8)
					{
						stampa("union all");
						stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 7");
						stampa("and CREATION_DATE>'2016-02-"+data+" 01:00:00.000'");
						stampa("and CREATION_DATE<'2016-02-"+data+" 01:29:59.999'");
						stampa("and text like '%"+artista+"%')");
						conta++;
					}
					
					else
					{
						stampa("union all");
						stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 7");
						stampa("and CREATION_DATE>'2016-02-"+data+" 00:00:00.000'");
						stampa("and CREATION_DATE<'2016-02-"+data+" 00:29:59.999'");
						stampa("and text like '%"+artista+"%')");
						conta++;
					}
					
					
				}
				else
				{
					stampa("union all");
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 7");
					stampa("and CREATION_DATE>'2016-02-"+data+" "+ora+":00:00.000'");
					stampa("and CREATION_DATE<'2016-02-"+data+" "+ora+":29:59.999'");
					stampa("and text like '%"+artista+"%')");
					conta++;
					
				}
				
			}
			
			else
				if(conta==7)
				{
					stampa("union all");
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 7");
					stampa("and CREATION_DATE>'2016-02-"+data+" 00:30:00.000'");
					stampa("and CREATION_DATE<'2016-02-"+data+" 00:59:59.999'");
					stampa("and text like '%"+artista+"%')");
					conta++;
				}
			if(conta==9)
			{
				stampa("union all");
				stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 7");
				stampa("and CREATION_DATE>'2016-02-"+data+" 01:30:00.000'");
				stampa("and CREATION_DATE<'2016-02-"+data+" 01:59:59.999'");
				stampa("and text like '%"+artista+"%')");
				conta++;
				i=frazioni;
			}
			{
				if(conta<6 && (conta%2)!=0)
				{
					stampa("union all");
					stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 7");
					stampa("and CREATION_DATE>'2016-02-"+data+" "+ora+":30:00.000'");
					stampa("and CREATION_DATE<'2016-02-"+data+" "+ora+":59:59.999'");
					stampa("and text like '%"+artista+"%')");
					conta++;
				}
					
				
				
			}
			
			
		}
			
			
		
		
			
		
	}
	
	
	public static void query_con_frazione(int intervallo, int data, String keyword)
	{
		
		//PER INTERVALLO SI INTENDE INTERVALLO DI MINUTI
		double frazioniDiOra=60/intervallo;	
		while(60%intervallo!=0)
		{
			//INSERIRE UN ALTRO INTERVALLO_IN_MINUTI CHE SIA SOTTOMULTIPLO DI 60 (AVVERTIRE L'UTENTE)
			intervallo=(Integer.parseInt(JOptionPane.showInputDialog("Inserire un sottomultiplo di 60 come intervallo di minuti:")));
			frazioniDiOra=60/intervallo;
		}
		
		
		int frazioniDiSerata = (int)(frazioniDiOra)*5;
		int frazioniDiOraInt = (int) (frazioniDiOra);
		// 5 E' IL NUMERO DI ORE IN UNA SERATA
		int ora = 21;
		String [] minuti = new String[60]; minuti[0]="00";
		//AL POSTO DI QUESTO ARRAY CREARE UNA MATRICE 5X9 IN MODO DA AVERE GIà TUTTE LE COMB. DI MINUTI
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
		
		for(int k = 0;k<frazioniDiSerata;k++)
		{
			
			
			if((conta_query%frazioniDiOraInt)==0 && conta_query!=0)
			{
				ora++; 
				i=0;
			}

			if(ora==24 && cambio_data==false)
			{
				ora=0;
				data++;
				cambio_data=true;
			}
			
			if(ora<10)
			{
				stampa("union all");
				stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 7");
				stampa("and CREATION_DATE>'2016-02-"+data+" 0"+ora+":"+minuti[i]+":00.000'");
				stampa("and CREATION_DATE<'2016-02-"+data+" 0"+ora+":"+minuti[i+intervallo-1]+":59.999'");
				stampa("and text like '%"+keyword+"%')");
				conta_query++;
				i = i+intervallo;
			}

			else
			{
				if(conta_query>0) stampa("union all");
				stampa("(select count(*) as num_tweet from [TvPad2-dev].dbo.TWEET where REQUEST_ID = 7");
				stampa("and CREATION_DATE>'2016-02-"+data+" "+ora+":"+minuti[i]+":00.000'");
				stampa("and CREATION_DATE<'2016-02-"+data+" "+ora+":"+minuti[i+intervallo-1]+":59.999'");
				stampa("and text like '%"+keyword+"%')");
				conta_query++;
				i = i+intervallo;
				
				
				
			}
			
			
		}
		
		
		
		
		
		
			
		
		
		
	}
	
	
	
	
	
	
	
	
	public static void stampa(String stringa)
	{
		System.out.println(stringa);
	}
	
	public static void main(String []args)
	{
		String keyword = JOptionPane.showInputDialog("Inserisci il nome della keyword:");
		String start = JOptionPane.showInputDialog("Inserisci data di inizio run (AAAA-MM-GG):");
		String start_time = JOptionPane.showInputDialog("Inserisci orario di inizio run (hh-mm-ss):");
		String end_time = JOptionPane.showInputDialog("Inserisci orario di inizio run (hh-mm-ss):");
		
		String end = JOptionPane.showInputDialog("Inserisci data di fine run (AAAA-MM-GG):");
		boolean controllo = false;

		do		//DO-WHILE CHE RIEPTE LE OPERAZIONI DI CONTROLLO DEI DATI TEMPORALI
		{

			for(int i = 0; i<start.length(); i++)
			{
				
				if(start.charAt(4)!='-' || start.charAt(7)!='-' || (start.charAt(i)!= '0' && start.charAt(i)!= '1' && start.charAt(i)!= '2' && start.charAt(i)!= '3' && start.charAt(i)!= '4' && start.charAt(i)!= '5' && start.charAt(i)!= '6' && start.charAt(i)!= '7' && start.charAt(i)!= '8' && start.charAt(i)!= '9'))
				{	
					start = JOptionPane.showInputDialog("Errore nell'inserimento della data. Inserire nel formato AAAA-MM-GG:");
					i=0;
				}
			}

			for(int i = 0; i<end.length(); i++)
			{
				
				if(end.charAt(4)!='-' || end.charAt(7)!='-' || (end.charAt(i)!= '0' && end.charAt(i)!= '1' && end.charAt(i)!= '2' && end.charAt(i)!= '3' && end.charAt(i)!= '4' && end.charAt(i)!= '5' && end.charAt(i)!= '6' && end.charAt(i)!= '7' && end.charAt(i)!= '8' && end.charAt(i)!= '9'))
				{	
					end = JOptionPane.showInputDialog("Errore nell'inserimento della data. Inserire nel formato AAAA-MM-GG:");
					i=0;
				}
			}


			for(int i = 0; i<start_time.length(); i++)
			{
				
				if(start_time.charAt(2)!=':' || start_time.charAt(5)!=':' || (start_time.charAt(i)!= '0' && start_time.charAt(i)!= '1' && start_time.charAt(i)!= '2' && start_time.charAt(i)!= '3' && start_time.charAt(i)!= '4' && start_time.charAt(i)!= '5' && start_time.charAt(i)!= '6' && start_time.charAt(i)!= '7' && start_time.charAt(i)!= '8' && start_time.charAt(i)!= '9'))
				{	
					start_time = JOptionPane.showInputDialog("Orario errato. Inserire nel formato hh:mm:ss.");
					i=0;
				}
			}


			for(int i = 0; i<end_time.length(); i++)
			{
				
				if(end_time.charAt(2)!=':' || end_time.charAt(5)!=':' || (end_time.charAt(i)!= '0' && end_time.charAt(i)!= '1' && end_time.charAt(i)!= '2' && end_time.charAt(i)!= '3' && end_time.charAt(i)!= '4' && end_time.charAt(i)!= '5' && end_time.charAt(i)!= '6' && end_time.charAt(i)!= '7' && end_time.charAt(i)!= '8' && end_time.charAt(i)!= '9'))
				{	
					end_time = JOptionPane.showInputDialog("Orario errato. Inserire nel formato hh:mm:ss.");
					i=0;
				}
			}


		String start_day1=""+Integer.toString(start.charAt(8))+Integer.toString(start.charAt(9)), 
		start_month1=""+Integer.toString(start.charAt(5))+Integer.toString(start.charAt(6)), 
		start_year1=""+Integer.toString(start.charAt(0))+Integer.toString(start.charAt(1))+Integer.toString(start.charAt(2))+Integer.toString(start.charAt(3));

		String end_day1=""+Integer.toString(end.charAt(8))+Integer.toString(end.charAt(9)), 
		end_month1=""+Integer.toString(end.charAt(5))+Integer.toString(end.charAt(6)), 
		end_year1=""+Integer.toString(end.charAt(0))+Integer.toString(end.charAt(1))+Integer.toString(end.charAt(2))+Integer.toString(end.charAt(3));
		
		int start_day=Integer.parseInt(start_day1),
		start_month=Integer.parseInt(start_month1),
		start_year=Integer.parseInt(start_year1);
		
		int end_day=Integer.parseInt(end_day1),
		end_month=Integer.parseInt(end_month1),
		end_year=Integer.parseInt(end_year1);
		
		String start_hh1=""+Integer.toString(start_time.charAt(0))+Integer.toString(start_time.charAt(1)), 
				start_mm1=""+Integer.toString(start_time.charAt(3))+Integer.toString(start_time.charAt(4)), 
				start_ss1=""+Integer.toString(start_time.charAt(6))+Integer.toString(start_time.charAt(7));
		
		String end_hh1=""+Integer.toString(end_time.charAt(0))+Integer.toString(end_time.charAt(1)), 
				end_mm1=""+Integer.toString(end_time.charAt(3))+Integer.toString(end_time.charAt(4)), 
				end_ss1=""+Integer.toString(end_time.charAt(6))+Integer.toString(end_time.charAt(7));

		int start_hh=Integer.parseInt(start_hh1),
				start_mm=Integer.parseInt(start_mm1),
				start_ss=Integer.parseInt(start_ss1);
		
		int end_hh=Integer.parseInt(end_hh1),
				end_mm=Integer.parseInt(end_mm1),
				end_ss=Integer.parseInt(end_ss1);
		
		
		
		//CONTROLLARE CHE DATA E ORA SIANO COMPATIBILI (END DEVE ESSERE SUCCESSIVO A START)

		if(end_year>=start_year)
			if(end_month>=start_month)
				if(end_day>=start_day)
					if(end_hh>=start_hh)
						if(end_mm>=start_mm)
							if(end_ss>=start_ss)
								controllo = true;
		
		


		}

		while(controllo==false);{	//QUESTO WHILE RIPETE TUTTE LE OPERAZIONI FINCHE' ORA E GIORNO SONO CORRETTI E COERENTI	
		int numeroDiOre;
		String [] giorni = new String [366];
		giorni[0]="-1";
		String decina = "0";
		int giorno = 1;
		for(int i = 0; i<giorni.length; i++)
		{
			if(i>=1 && i<=31)
				if(i<10)
					giorni[i]="01-"+decina+i;
				else	
					giorni[i]="01-"+i;
			
			if(i>31 && i<=(31+28))
				giorni[i]="02-"+i;
		
			if(i>(31+28) && i<=(31+28+31))
				giorni[i]="03-"+i;

			if(i>(31+28+31) && i<=(31+28+31+30))
				giorni[i]="04-"+i;

			if(i>(31+28+31+30) && i<=(31+28+31+30+31))
				giorni[i]="05-"+i;

			if(i>(31+28+31+30+31) && i<=(31+28+31+30+31+30))
				giorni[i]="06-"+i;

			if(i>(31+28+31+30+31+30) && i<=(31+28+31+30+31+30+31))
				giorni[i]="07-"+i;

			if(i>(31+28+31+30+31+30+31) && i<=(31+28+31+30+31+30+31+31))
				giorni[i]="08-"+i;

			if(i>(31+28+31+30+31+30+31+31) && i<=(31+28+31+30+31+30+31+31+30))
				giorni[i]="09-"+i;

			if(i>(31+28+31+30+31+30+31+31+30) && i<=(31+28+31+30+31+30+31+31+30+31))
				giorni[i]="10-"+i;

			if(i>(31+28+31+30+31+30+31+31+30+31) && i<=(31+28+31+30+31+30+31+31+30+31+30))
				giorni[i]="11-"+i;

			if(i>(31+28+31+30+31+30+31+31+30+31+30) && i<=(31+28+31+30+31+30+31+31+30+31+30+31))
				giorni[i]="12-"+i;

			giorno++;
			
			if(i==31 || i == (31+28) || i == (31+28+31) || i == (31+28+31+30) || i == (31+28+31+30+31) || i == (31+28+31+30+31+30) || i == (31+28+31+30+31+30+31) || i == (31+28+31+30+31+30+31+31) || i == (31+28+31+30+31+30+31+31+30) || i == (31+28+31+30+31+30+31+31+30+31) || i == (31+28+31+30+31+30+31+31+30+31+30) || i == (31+28+31+30+31+30+31+31+30+31+30+31))
				giorno=1;
								
		} 
		
	
		//int data = 10;
		/*
		for(int i=0;i<4;i++)
		{
			if(i!=0) System.out.println("union all");
			query(artista, data);
			data++;
			
		}
		
		*/
		int intervallo = Integer.parseInt(JOptionPane.showInputDialog("Inserire intervallo di minuti:"));
		//query_con_frazione(intervallo, data, keyword);
		}
			
		
	}
}
