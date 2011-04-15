
function toggle(showHideDiv, switchTextDiv)
{
	var ele = document.getElementById(showHideDiv);
	var text = document.getElementById(switchTextDiv);
	if(ele.style.display == "block")
	{
    		ele.style.display = "none";
  	}
	else
	{
		ele.style.display = "block";
	}
}