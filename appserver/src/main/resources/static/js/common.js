function initPosts(name){
    var dt = $('#posts').DataTable( {
        "language": datatable_lang,
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url":"/js/data.json",
            "dataSrc":function ( json ) {
                console.log(json);
                console.log(dt.columns)
                return json.data;
            },
            // "data": function ( d ) {
            //     return JSON.stringify(d);
            // }
        }
    } );
}