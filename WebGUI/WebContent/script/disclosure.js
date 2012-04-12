/**
 * @author alice
 */
var disclosure = function(){

    var closedArrow = '\u25b8', openArrow = '\u25be';
    
    /**
     * My own component object
     */
    var component = null;
    
    /**
     * The identifier for the disclosure widgets
     */
    var disclosureId = null;
    
    /**
     * Gets the current title and arrow of a disclosure widget
     * @param {Object} feature
     */
    function getTitleAndArrow(feature){
        var html = feature.html();
        return {
            title: html.substring(2, html.length),
            firstChar: html.substring(0, 1)
        };
    }
    
    function open(feature){
        var ta = getTitleAndArrow(feature);
        feature.html(openArrow + " " + ta.title);
        feature.next().show();
    }
    
    function initialize(){
        var hash = document.location.hash.toString(), anchorId = hash.substring(1, hash.length), anchor = null;
        
        $(disclosureId).prepend(closedArrow + " ");
        $(disclosureId).click(function(){
            var ta = getTitleAndArrow($(this)), disclosureTitle = ta.title, firstChar = ta.firstChar;
            if (firstChar == closedArrow) {
                $(this).html(openArrow + " " + disclosureTitle);
            }
            else {
                $(this).html(closedArrow + " " + disclosureTitle);
            };
            $(this).next().toggle('fast');
            return false;
        }).next().hide();
        
        if (anchorId) {
            $(disclosureId).each(function(){
                if ($(this).is("#" + anchorId)) {
                    anchor = $(this);
                    return false;
                }
            });
        }
        
//        // If the anchor is a feature/disclosure, open it
//        // Otherwise, open the first feature/disclosure
//        if (anchor) {
//            open(anchor);
//        }
//        else {
//            $(disclosureId).first().click();
//        }
        
        // Regardless of whether or not the anchor is a 
        // feature/disclosure, scroll to it
        if (anchorId) {
            anchor = document.getElementById(anchorId);
            if (anchor) {
                anchor.scrollIntoView();
            }
        }
    }
    
    /**
     * Called when a /container/load event is received.
     * @param {Object} event
     */
    function callback(event){
        initialize();
    }
    
    return {
    
        //*********************//
        // Component Interface //
        //*********************//
        
        /**
         * Method returning the component's <b>unique</b>
         * name. Using a fully qualified name is encouraged.
         * @return the component unique name
         */
        getComponentName: function(){
            return 'de_akquinet_product_disclosure';
        },
        
        /**
         * Configure method. This method is called when the
         * component is registered on the hub.
         * @param theHub the hub
         * @param configuration the configuration
         */
        configure: function(theHub, configuration){
            hub = theHub;
            disclosureId = configuration.disclosureId;
        },
        
        /**
         * The Start function
         * This method is called when the hub starts or just
         * after configure if the hub is already started.
         */
        start: function(){
            component = this;
            
            // Then, we subscribe to the /container/load topic
            hub.subscribe(this, "/container/load", callback);
            
        },
        
        /**
         * The Stop method is called when the hub stops or
         * just after the component removal if the hub is
         * not stopped. No events can be sent in this method.
         */
        stop: function(){
        
        }
    }
    
}
