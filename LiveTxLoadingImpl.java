package cz.monetplus.asors.ptlf_java;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cz.monetplus.asors.repo.asors.dao.TransactBuffDao;
import cz.monetplus.asors.repo.asors.domain.TransactBuff;
import cz.monetplus.asors.repo.asors.domain.TransactPK;
import cz.monetplus.asors.repo.asors.service.TransactBuffService;
import cz.monetplus.asors.repo.merchant.service.TxLiveService;

@Service("liveTxLoading")
public class LiveTxLoadingImpl implements LiveTxLoading {
	
	@Autowired
	private TransactBuffDao transactBuffDao;

	@Autowired
	private TransactBuffService transactBuffService;

	@Autowired
	private TxLiveService txLiveService;
	
	/* (non-Javadoc)
	 * @see cz.monetplus.asors.ptlf_java.TxLiveTxLoading#run()
	 */
	public void run() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -2);

		// transakce
		TransactPK tpk1 = new TransactPK("M000000001", cal.getTime());
		TransactBuff buff1 = new TransactBuff();
		buff1.setId(tpk1);
		buff1.setLastChange(cal.getTime());
		buff1.setExported(0);
		transactBuffDao.save(buff1);

		cal.add(Calendar.MINUTE, -10);
		tpk1 = new TransactPK("M000000001", cal.getTime());
		TransactBuff buff2 = new TransactBuff();
		buff2.setExported(0);
		buff2.setLastChange(cal.getTime());
		buff2.setId(tpk1);
		transactBuffDao.save(buff2);

		// nacitaji se transakce starsi nez 1 min a novejsi nez 5 min
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE, -1);
		Calendar cal2 = GregorianCalendar.getInstance();
		cal2.setTime(new Date());
		cal2.add(Calendar.MINUTE, -6);
		List<TransactBuff> tList = transactBuffDao.findAll();
		// Assert.assertEquals(2, tList.size());
		tList = transactBuffService.findForLiveExport(cal2.getTime(),
				cal.getTime());
		// Assert.assLiveTxLoadingTest()ertEquals(1, tList.size());
		// ulozeni
		txLiveService.updateLiveTx(tList);
		// neni zavedeny terminal
		// Assert.assertEquals(0, txLiveService.count(new MerchantCrit()));

		// nacteni nefunguje u H2 db
	}

}
