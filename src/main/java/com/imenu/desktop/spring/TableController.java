package com.imenu.desktop.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TableController {

    FirebaseClient client;

    public TableController( FirebaseClient client ) {
        this.client = client;
    }

    @GetMapping("/tables/{name}/{status}")
    public void setTableStatus(@PathVariable String name, @PathVariable String status ) {
        Table table = client.getTable( name );
        if ( table == null )
            return;
        client.setTableStatus( table.getId(), status );
    }

}
