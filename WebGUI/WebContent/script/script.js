var mainContentUrl = 'mainCont.jsp';
var completeSiteUrl = 'inspector.jsp';
var questionStatusUrl = 'qStat.jsp';
var reportUrl = 'report.jsp';

var positiveQuestions = 0;
var negativeQuestions = 0;
var openQuestions = 0;


function restartQuestion()
{
	var xmlHttpObject = new XMLHttpRequest();

	xmlHttpObject.open('get', completeSiteUrl + '?action=restartQuestion', false);
	xmlHttpObject.send(null);
	
	updateMainContent();
}

function restartAllQuestions()
{
	location = completeSiteUrl + '?action=restartAllQuestions';
}

function isAvailable()
{
	var xmlHttpObject = new XMLHttpRequest();

	xmlHttpObject.open('get', questionStatusUrl + '?action=isAvailable', false);
	xmlHttpObject.send(null);
	
	if(xmlHttpObject.responseText == 'true')
	{
		return true;
	}
	else
	{
		return false;
	}
}

function isStale(target)
{
	var xmlHttpObject = new XMLHttpRequest();

	if(target == null || target == '')
	{
		xmlHttpObject.open('get', questionStatusUrl + '?action=isStale', false);
	}
	else
	{
		xmlHttpObject.open('get', questionStatusUrl + '?action=isStale&quest=' + target, false);
	}
	xmlHttpObject.send(null);
	
	if(xmlHttpObject.responseText == 'true')
	{
		return true;
	}
	else
	{
		return false;
	}
}

function getMainContent(target)
{
	var xmlHttpObject = new XMLHttpRequest();

	if(target == null || target == '')
	{
		xmlHttpObject.open('get', mainContentUrl, false);
	}
	else
	{
		xmlHttpObject.open('get', mainContentUrl + '?quest=' + target, false);
		$('[id^="navlink_"]').css("font-weight", "normal");
		$('#navlink_' + target).css("font-weight", "bold");
	}
	xmlHttpObject.send(null);
	
	return xmlHttpObject.responseText;
}

function updateMainContent(target)
{
	if(!isAvailable())
	{
		return;
	}
	
	if($('#messages') != 'undefined')
	{
		$('#messages').html('');
		if(isStale(target))
		{
			$('#messages').html(_('Eine für diese Frage relevante Resource hat wurde geändert. Bitte werten Sie diese erneut aus.'));
		}
	}
	
	var responseText = getMainContent(target);

	if (mainCont != responseText)
	{
		mainCont = responseText;
		$('#right #content').html(responseText);

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
	if(!isAvailable())
	{
		return;
	}
	
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

	xmlHttpObject.open('get', questionStatusUrl + '?action=questStatus&quest=' + questId, false);
	xmlHttpObject.send(null);

	var questStatus = xmlHttpObject.responseText;

	if (questStatus == 'pos')
	{
		$('#upper-left #' + questId).html('<a onmouseover="showWMTT(\'goodTip\')" onmouseout="hideWMTT()" href=""><img src="img/good.png" />');
		//$('#upper-left #' + questId).html('<span class="good">' + pos + '</span>');
		positiveQuestions++;
	}
	else if (questStatus == 'neg')
	{
		$('#upper-left #' + questId).html('<a onmouseover="showWMTT(\'badTip\')" onmouseout="hideWMTT()" href=""><img src="img/bad.png" />');
		negativeQuestions++;
	}
	else if (questStatus == 'ope')
	{
		$('#upper-left #' + questId).html('<a onmouseover="showWMTT(\'openTip\')" onmouseout="hideWMTT()" href=""><img src="img/open.png" />');
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

document.onmousemove = updateWMTT;
var wmtt = null;

function showWMTT(id)
{
	wmtt = document.getElementById(id);
	wmtt.style.display = "block";
}

function updateWMTT(e)
{
	if (wmtt != null && wmtt.style.display == 'block')
	{
		wmtt.style.left = (window.event.x + wmtt.offsetParent.scrollLeft + 20) + "px";
		wmtt.style.top = (window.event.y + wmtt.offsetParent.scrollTop + 20) + "px";
	}
}

function hideWMTT()
{
	wmtt.style.display = "none";
}

function showReport()
{
	var questionCount = positiveQuestions + negativeQuestions + openQuestions;
	
	if(questionCount == 0)
	{
		alert(noQuestions);
	}
	else if(openQuestions != 0)
	{
		alert(stillOpenQuestions);
	}
	else
	{
		location = reportUrl;
	}
}