var mainContentUrl = 'mainCont.jsp';
var questionStatusUrl = 'qStat.jsp';
var mainContHash = '';

var positiveQuestions = 0;
var negativeQuestions = 0;
var openQuestions = 0;

setInterval(updateMainContent, 3000);
setInterval(updateQuestions, 1000);

function updateMainContent()
{
	var xmlHttpObject = new XMLHttpRequest();

	xmlHttpObject.open('get', mainContentUrl, false);
	xmlHttpObject.send(null);

	var responseHash = str_md5(xmlHttpObject.responseText);

	if (mainContHash != responseHash)
	{
		mainContHash = responseHash;
		$('#right #content').html(xmlHttpObject.responseText);

		hub.unregisterComponent('disclosure').start();

		hub.registerComponent(disclosure(), {
			disclosureId : '.disclosures .feature-title',
			component_name : 'disclosure'
		}).start();

		hub.publish(true, "/container/load", {
			containerId : 'body'
		});
	}
}

function updateQuestions()
{
	positiveQuestions = 0;
	negativeQuestions = 0;
	openQuestions = 0;

	var xmlHttpObject = new XMLHttpRequest();

	xmlHttpObject.open('get', questionStatusUrl, false);
	xmlHttpObject.send(null);

	questIds = xmlHttpObject.responseText.split(',');

	for ( var i = 0; i < questIds.length; ++i)
	{
		updateQuest(questIds[i]);
	}

	updateStatistics();
}

function updateQuest(questId)
{
	var xmlHttpObject = new XMLHttpRequest();

	xmlHttpObject.open('get', questionStatusUrl + '?quest=' + questId, false);
	xmlHttpObject.send(null);

	var questStatus = xmlHttpObject.responseText;

	if (questStatus == 'pos')
	{
		$('#upper-left #' + questId).html('<span class="good">' + pos + '</span>');
		positiveQuestions++;
	}
	else if (questStatus == 'neg')
	{
		$('#upper-left #' + questId).html('<span class="bad">' + neg + '</span>');
		negativeQuestions++;
	}
	else if (questStatus == 'ope')
	{
		$('#upper-left #' + questId).html('<span class="open">' + ope + '</span>');
		openQuestions++;
	}
}

function updateStatistics()
{
	var questionCount = positiveQuestions + negativeQuestions + openQuestions;

	
	if(questionCount == 0)
	{
		$('#middle-left .good').html('--%');
		$('#middle-left .bad').html('--%');
		$('#middle-left .open').html('--%');
	}
	else
	{
		var goodPercentage = Math.round((100.0 * positiveQuestions) / questionCount);
		var badPercentage = Math.round((100.0 * negativeQuestions) / questionCount);
		var openPercentage = Math.round((100.0 * openQuestions) / questionCount);
		
		// correct the 2 possible round errors
		// if 2 percantages are xx.5 then the sum grows over 100
		if (goodPercentage + badPercentage + openPercentage > 100)
		{
			if (goodPercentage > 0)
			{
				--goodPercentage;
			}
			else if (openPercentage > 0)
			{
				--openPercentage;
			}
			else if (badPercentage > 0)
			{
				--badPercentage;
			}
		}
		// if all percantages are xx.y with y < .5 then the sum is 99
		else if (goodPercentage + badPercentage + openPercentage < 100)
		{
			if (badPercentage < 100)
			{
				++badPercentage;
			}
			else if (openPercentage < 100)
			{
				++openPercentage;
			}
			else if (goodPercentage < 100)
			{
				++goodPercentage;
			}
		}
		
		$('#middle-left .good').html('' + goodPercentage + '%');
		$('#middle-left .bad').html('' + badPercentage + '%');
		$('#middle-left .open').html('' + openPercentage + '%');
	}
}

function showWMTT(id)
{
	wmtt = document.getElementById(id);
	wmtt.style.display = "block";
}

document.onmousemove = updateWMTT;

function updateWMTT(e)
{
	if (wmtt != null && wmtt.style.display == 'block')
	{
		x = (e.pageX ? e.pageX : window.event.x) + wmtt.offsetParent.scrollLeft - wmtt.offsetParent.offsetLeft;
		y = (e.pageY ? e.pageY : window.event.y) + wmtt.offsetParent.scrollTop - wmtt.offsetParent.offsetTop;
		wmtt.style.left = (x + 20) + "px";
		wmtt.style.top = (y + 20) + "px";
	}
}

function hideWMTT()
{
	wmtt.style.display = "none";
}
