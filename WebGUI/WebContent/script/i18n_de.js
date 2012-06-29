
var i18n = {
	"Eine für diese Frage relevante Resource hat wurde geändert. Bitte werten Sie diese erneut aus.": "Eine für diese Frage relevante Resource hat wurde geändert. Bitte werten Sie diese erneut aus."
};

function _(string)
{
	if (typeof(i18n) != 'undefined' && i18n[string])
	{
		return i18n[string];
	}
	
	return string;
}