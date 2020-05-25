package ivgroup.master.database.scheduler;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import ivgroup.master.database.dao.impl.CompanyExecutivePLDAOImpl;
import ivgroup.master.database.dao.impl.TicketDAOImpl;
import ivgroup.master.database.dto.companyExecutivePL.CompanyExecutivePLInsert;
import ivgroup.master.database.dto.scheduler.ScheduerCompanyExecutivePLUpdateInsert;

@Configuration
@EnableScheduling
public class CompanyExecutivePLUpdateScheduler 
{
	@Autowired
	TicketDAOImpl tdl;
	
	@Autowired
	CompanyExecutivePLDAOImpl cpldl;
	
	Thread t=null;
	
	@Scheduled(cron = "0 0 0 * * ?")
	public void schedulerForFollowupDate()
	{
		t=new Thread()
		{
			
			public void run()
			{
				
				List<ScheduerCompanyExecutivePLUpdateInsert> companyExecutiveList=new ArrayList<ScheduerCompanyExecutivePLUpdateInsert>();
				try {
					companyExecutiveList=tdl.selectCompanyExecutivePLUpdates(new Date(System.currentTimeMillis()));
					ListIterator<ScheduerCompanyExecutivePLUpdateInsert> li=companyExecutiveList.listIterator();
					while(li.hasNext())
					{
						ScheduerCompanyExecutivePLUpdateInsert record=li.next();
						Double workPercentage=(record.getCurrentWorkProgress()*100)/(double)record.getThresholdWorkProgress();
						Integer pLrate=0;
						if(workPercentage>=0&&workPercentage<10)
						{
							pLrate=-7;
						}
						else if(workPercentage>=10&&workPercentage<20)
						{
							pLrate=-4;
						}
						else if(workPercentage>=20&&workPercentage<30)
						{
							pLrate=-3;
						}
						else if(workPercentage>=30&&workPercentage<40)
						{
							pLrate=-2;
						}
						else if(workPercentage>=40&&workPercentage<50)
						{
							pLrate=-1;
						}
						else if(workPercentage>=50&&workPercentage<60)
						{
							pLrate=1;
						}
						else if(workPercentage>=60&&workPercentage<70)
						{
							pLrate=2;
						}
						else if(workPercentage>=70&&workPercentage<80)
						{
							pLrate=3;
						}
						else if(workPercentage>=80&&workPercentage<90)
						{
							pLrate=4;
						}
						else if(workPercentage>=90&&workPercentage<100)
						{
							pLrate=5;
						}
						else if(workPercentage==100)
						{
							pLrate=10;
						}
						
						cpldl.addCompanyExecutivePL(new CompanyExecutivePLInsert(
																				record.getCompanyExecutiveId(),
																				 pLrate,
																				 new Timestamp(System.currentTimeMillis())));
					}
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		};
		t.start(); 
	}
	
}