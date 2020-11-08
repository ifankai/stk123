/**
 * 
 */
$.ajax({
		  type: 'get',
		  url: 'http://hqdigi2.eastmoney.com/EM_Quote2010NumericApplication/CompatiblePage.aspx?Type=OB&stk=6000301&Reference=xml&limit=0&page=1',
	      success: function (data) {
			if(data > 0){
				eval(data);
				alert(jsTimeSharingData);
			}
		  }
		});