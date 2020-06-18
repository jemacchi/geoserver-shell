.. image:: https://travis-ci.org/jericks/geoserver-shell.svg?branch=master
    :target: https://travis-ci.org/jericks/geoserver-shell

GeoServer Shell
===============
Administer Geoserver using a command line interface (CLI).  Geoserver Shell uses the same shell interface used by Spring Roo and
provides extensive tab completion, history support, and the ability to run scripts.

Geserver Shell administers Geoserver using the excellent Geoserver Rest API.  You can publish shapefiles, GeoTIFFs, and PostGIS layers,
upload and download SLDs, and start tile seeding straight from the command line.

You can use Geoserver Shell interactively by typing **gs-shell** at the command line
or you can write scripts and then execute them from the command line by typing **gs-shell --cmdfile script.gs**
or by using the **script --file script.gs** command within a Geoserver Shell session.

Build
-----

Clone the repository::

    git clone https://github.com/jemacchi/geoserver-shell

Build the code::

    mvn clean install

Run the geoserver-shell::

    target/gs-shell-app/gs-shell-1.0-SNAPSHOT/bin/gs-shell

Build your docker image for Geoserver-shell
-------------------------------------------

Clone the repository::

    git clone https://github.com/jemacchi/geoserver-shell

Build code and docker image::

    ./build.sh

Then you can create a Docker container from created image.
    
Examples using Geoserver-shell
------------------------------

Create a Workspace::

    gs-shell>geoserver set --url http://localhost:8080/geoserver --user admin --password geoserver

    gs-shell>workspace list
    cite
    it.geosolutions
    nurc
    sde
    sf
    tiger
    topp

    gs-shell>workspace create --name test
    true

    gs-shell>exit

Publish a shapefile::

    gs-shell>geoserver set --url http://localhost:8080/geoserver
    gs-shell>workspace create --name naturalearth
    gs-shell>shapefile zip --shapefile NaturalEarth/SmallScale/110m_cultural/110m_admin_0_countries.shp
    gs-shell>shapefile publish --workspace naturalearth --file NaturalEarth/SmallScale/110m_cultural/110m_admin_0_countries.zip

Upload a style::

    gs-shell>style create --file NaturalEarth/SmallScale/110m_cultural/110m_admin_0_countries.sld --name 110m_admin_0_countries
    gs-shell>layer style add --name 110m_admin_0_countries --style 110m_admin_0_countries
    gs-shell>layer modify --name 110m_admin_0_countries --defaultStyle 110m_admin_0_countries

Start seeding tiles::

    gs-shell>gwc seed --name naturalearth:110m_admin_0_countries --gridset EPSG:4326 --start 0 --stop 4

Preview a newly uploaded layer::

    gs-shell>geoserver getmap --layers states
    map.png
    gs-shell>! open map.png

    gs-shell>geoserver getfeature --typeName topp:states --propertyname STATE_NAME --featureid states.49
    FID,STATE_NAME
    states.49,Washington

Commands
--------

* built ins

    * exit = exit the shell

    * quit = exit the shell

    * help = list all commands and their usages

    * ! = run os command string

    * date = displays local date and time

    * script = run a script

    * system properties = show shell's properties

    * version = display current cli version

* geoserver

    * geoserver set --url http://localhost:8080/geoserver --user admin --password geoserver

    * geoserver show

    * geoserver reload

    * geoserver reset

    * geoserver backup --directory backup1 --includedata false --includegwc false --includelog true

    * geoserver restore --directory backup1

    * geoserver getmap --layers states

    * geoserver getlegend --layer states

    * geoserver getfeature --typeName topp:states

* about

    * version list

    * manifest list

    * manifest get

* workspace

    * workspace list

    * workspace create --name test

    * workspace get --name test

    * workspace delete --name test

    * workspace default get

    * workspace default set --name test

* namespace

    * namespace list

    * namespace create --prefix test --uri test.com

    * namespace get --prefix test

    * namespace modify --prefix test --uri test2.com

    * namespace delete --prefix test --recurse true

    * namespace default get

    * namespace default set --prefix test

* style

    * style list

    * style list --workspace topp

    * style get --name line

    * style sld get --name line

    * style sld get --name line --file line.sld

    * style create --name new_line --file line.sld

    * style modify --name new_line --file line.sld

    * style delete --name new_line

* template

    * template add --file title.ftl --workspace topp --datastore states_shapefile --featuretype states --name title

    * template list --workspace topp --datastore states_shapefile --featuretype states

    * template get --name title.ftl --workspace topp --datastore states_shapefile --featuretype states

    * template get --name title.ftl --workspace topp --datastore states_shapefile --featuretype states --file new_title.ftl

    * template modify --file title.ftl --workspace topp --datastore states_shapefile --featuretype states --name title

    * template delete --name title.ftl --workspace topp --datastore states_shapefile --featuretype states

* font

    * font list

    * font list --search Arial

* datastore
    
    * datastore list --workspace topp

    * datastore get --workspace topp --name taz_shapes

    * datastore create --workspace topp --name h2test --connectionParams "dbtype=h2 database=test.db"
    
    * datastore modify --workspace topp --name h2test --description Testing

    * datastore delete --workspace topp --name h2test --recurse false
    
    * datastore upload --workspace topp --name states_convexhull --type shp --file states_convexhull.zip

* shapefile

    * shapefile zip --shapefile states_voronoi.shp --zipfile states_voronoi.zip

    * shapefile publish --workspace topp --datastore states_voronoi --layer states_voronoi --file states_voronoi.zip

* postgis

    * postgis datastore create --workspace topp --datastore postgis --host localhost --port 5432 --database postgis --schema public --user uzer --password pass

    * postgis featuretype publish --workspace topp --datastore postgis --table world_boundaries

* featuretype

    * featuretype list topp --datastore taz_shapes

    * featuretype list --workspace post --datastore postgis --list available

    * featuretype get --workspace topp --datastore taz_shapes --featuretype tasmania_cities

    * featuretype publish --workspace postgis --datastore tables --featuretype table

    * featuretype create --workspace topp --datastore taz_shapes --featuretype taz_hydro --schema "the_geom:LineString:srid=4326,name:String,id:int"

    * featuretype modify --workspace topp --datastore taz_shapes --featuretype taz_hydro --name "Tazmania Hydro Lines"

    * featuretype delete --workspace topp --datastore taz_shapes --featuretype taz_hydro --recurse true

* coverage stores

    * coverage store list --workspace nurc
      
    * coverage store get --workspace nurc --coveragestore mosaic

    * coverage store upload --workspace nurc --coveragestore test --file alki.tif --type geotiff

    * coverage store delete --workspace nurc --coveragestore test --recurse true

    * coverage store create --workspace nurc --name raster --type GeoTiff --url file:coverages/raster/raster.tif

    * coverage store modify --workspace nurc --coveragestore raster --enabled false
    
* coverage
  
    * coverage list --workspace nurd --coveragestore mosaic

    * coverage get --workspace nurc --coveragestore mosaic --coverage mosaic

    * coverage create --workspace nurc --coveragestore worldImageSample --coverage test

    * coverage delete --workspace nurc --coveragestore worldImageSample --coverage test --recurse true

    * coverage modify --workspace nurc --coveragestore raster --coverage raster --title "My Raster"

* worldimage

    * worldimage zip --file NaturalEarth/MediumScale/GRAY_50M_SR_OB/GRAY_50M_SR_OB.tif

    * worldimage publish --file GRAY_50M_SR_OB/GRAY_50M_SR_OB.zip --workspace naturalearth --coveragestore myworld --coverage test

* layers

    * layer list

    * layer get --name states

    * layer modify --name states --title "United States"

    * layer delete --name states
    
    * layer style list --name states
    
    * layer style add --name states --style line

* ows
  
    * ows wcs list

    * ows wms list

    * ows wfs list

* ows wcs

    * ows wcs list --workspace topp

    * ows wcs create --workspace nurc

    * ows wcs modify --workspace nurc --enabled false
      
    * ows wcs delete --workspace topp

* ows wfs

    * ows wfs create --workspace topp

    * ows wfs list --workspace topp

    * ows wfs modify --workspace topp --enabled false

    * ows wfs delete --workspace topp

* ows wms 

    * ows wms create --workspace topp

    * ows wms list --workspace topp

    * ows wms modify --workspace topp --enabled false

    * ows wms delete --workspace topp

* settings

    * settings list

    * settings modify --person Jared

    * settings contact list

    * settings contact modify --city Tacoma

    * settings local list --workspace topp

    * settings local delete --workspace topp

    * settings local create --workspace topp

    * settings local modify --workspace topp --person "Jared Erickson"
   
* gwc
    
    * gwc layer list

    * gwc layer get --name topp:states

    * gwc wms layer create --name wms_states --wmsurl http://localhost:8080/geoserver/wms --wmslayers topp:states

    * gwc geoserver layer create --name topp:states

    * gwc layer delete --name test

    * gwc wms layer modify --name topp:AFREEMAN.TOWNS_ANF2 --gutter 20

    * gwc geoserver layer modify --name topp:states --enabled false

    * gwc status

    * gwc seed --name topp:states_voronoi --gridset EPSG:4326 --start 0 --stop 4

    * gwc status --name top:states_voronoi
      
    * gwc reseed --name topp:states_voronoi --gridset EPSG:4326 --start 0 --stop 4

    * gwc truncate --name topp:states_voronoi --gridset EPSG:4326 --start 0 --stop 4

    * gwc kill

* wmsstore

    * wmsstore list --workspace topp

    * wmsstore get --workspace topp --store massgis

    * wmsstore create --workspace topp --store massgis --url http://giswebservices.massgis.state.ma.us/geoserver/wms?request=GetCapabilities&version=1.1.0&service=wms

    * wmsstore modify --workspace topp --store massgis --enabled false

    * wmsstore delete --workspace topp --store massgis --recurse true

    * wmsstore layer list --workspace topp --store massgis

    * wmsstore available layer list --workspace topp --store massgis

    * wmsstore layer get --workspace topp --store massgis --layer AFREEMAN.TOWNS_ANF2

    * wmsstore layer create --workspace top --store massgis --layer massgis:GISDATA.BIKETRAILS_ARC

    * wmsstore layer modify --workspace topp --store massgis --layer massgis:WELLS.WELLS_PT --enabled false
      
    * wmsstore layer delete --workspace topp --store massgis --layer massgis:WELLS.WELLS_PT --recurse true

* scripting (community module and only available for GeoServer 2.6 and greather)

    * scripting wps list

    * scripting wps get --name --file

    * scripting wps create --name --file

    * scripting wps modify --name --file

    * scripting wps delete --name

    * scripting function list

    * scripting function get --name --file

    * scripting function create --name --file

    * scripting function modify --name --file

    * scripting function delete --name

    * scripting wfs tx list

    * scripting wfs tx get --name --file

    * scripting wfs tx create --name --file

    * scripting wfs tx modify --name --file

    * scripting wfs tx delete --name

    * scripting app list

    * scripting app get --name --file

    * scripting app create --name --file

    * scripting app modify --name --file

    * scripting app delete --name

    * scripting session list --ext

    * scripting session get --ext --name

    * scripting session create --ext

    * scripting session run --ext --name --script

* acl security

    * security acl get

    * security acl set --mode MIXED

    * security acl layers get

    * security acl layers create --resource "myworkspace.*.w" --role "ROLE_1,ROLE_2"

    * security acl layers modify --resource "myworkspace.*.w" --role "ROLE_1,ROLE_2,ROLE_3"

    * security acl layers delete --resource "myworkspace.*.w"

    * security acl services get

    * security acl services create --resource "wfs.GetFeature" --role "ROLE_1,ROLE_2"

    * security acl services modify --resource "wfs.GetFeature" --role "ROLE_1,ROLE_2,ROLE_3"

    * security acl services delete --resource "wfs.GetFeature"

    * security acl rest get

    * security acl rest create --resource "/**:HEAD" --role ADMIN

    * security acl rest modify --resource "/**:HEAD" --role "ADMIN,DEV"

    * security acl rest delete --resource "/**:HEAD"

Libraries
---------
Spring Shell:
    https://github.com/SpringSource/spring-shell

GeoServer Manager:
    https://github.com/geosolutions-it/geoserver-manager

GeoTools:
    http://www.geotools.org/

Presentations
-------------

`GeoServer Shell: Administer GeoServer using a CLI <http://www.slideshare.net/JaredErickson/geo-servershell>`_


License
-------
GeoServer Shell is open source and licensed under the MIT License.

