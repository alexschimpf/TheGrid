# TheGrid


Example xml:

```xml
<the_grid>
    <size num_rows="3" num_cols="3" />
    <player grid_row="0" grid_col="0" row="10" col="1" />
    <rooms>
      ...
  		<room row="2" col="2">
  		    <openings>
  		        <opening row="0" col="5" />
  		        <opening row="10" col="0" />
  		    </openings>
  		    <chain_pads>
  		        <chain_pad start_row="3" end_row="3" start_col="5" end_col="9" friction="false" />
  		        <chain_pad start_row="4" end_row="4" start_col="5" end_col="9" friction="false" />
  		        <chain_pad start_row="6" end_row="6" start_col="2" end_col="10" friction="false" />
  		        <chain_pad start_row="7" end_row="7" start_col="2" end_col="10" friction="false" />
  		        <chain_pad start_row="9" end_row="9" start_col="1" end_col="9" friction="false" />
  		        <chain_pad start_row="10" end_row="10" start_col="1" end_col="9" friction="false" />
  		    </chain_pads>
  		    <scripts>
  		        <script type="update_transport_chain">
  		            <custom>
  		                <chain_ids>
  		                    <id>2,2-1</id>
  		                    <id>2,2-2</id>
  		                    <id>2,2-3</id>
  		                    <id>2,2-4</id>
  		                    <id>2,2-5</id>
  		                    <id>2,2-6</id>
  		                    <id>2,2-7</id>
  		                    <id>2,2-8</id>
  		                    <id>2,2-9</id>
  		                    <id>2,2-10</id>
  		                    <id>2,2-11</id>
  		                    <id>2,2-12</id>
  		                    <id>2,2-13</id>
  		                    <id>2,2-14</id>
  		                    <id>2,2-15</id>
  		                    <id>2,2-16</id>
  		                    <id>2,2-17</id>
  		                    <id>2,2-18</id>
  		                    <id>2,2-19</id>
  		                    <id>2,2-20</id>
  		                    <id>2,2-21</id>
  		                    <id>2,2-22</id>
  		                    <id>2,2-23</id>
  		                    <id>2,2-24</id>
  		                    <id>2,2-25</id>
  		                </chain_ids>
  		            </custom>
  		        </script>
  		    </scripts>
  		    <entities>
  		        <entity type="rectangle">
  		            <custom>
  		                <texture_key>red</texture_key>
  		                <height_scale>3</height_scale>
  		            </custom>
  		            <locations>
  		                <location row="1" col="4" />
  		            </locations>
  		        </entity>
  		        <entity type="breakable_transport" row="10" col="0">
  		            <custom>
  		                <num_shots>3</num_shots>
  		                <transport_row>0</transport_row>
  		                <transport_col>5</transport_col>
  		            </custom>
  		        </entity>
  		        <entity type="transport_chain">
  		            <custom>
  		                <transport_row>0</transport_row>
  		                <transport_col>5</transport_col>
  		            </custom>
  		            <locations>
  		                <location row="3" col="5" id="2,2-1" />
  		                <location row="3" col="6" id="2,2-2" />
  		                <location row="3" col="7" id="2,2-3" />
  		                <location row="3" col="8" id="2,2-4" />
  		                <location row="3" col="9" id="2,2-5" />
  		                <location row="6" col="10" id="2,2-6" />
  		                <location row="6" col="9" id="2,2-7" />
  		                <location row="6" col="8" id="2,2-8" />
  		                <location row="6" col="7" id="2,2-9" />
  		                <location row="6" col="6" id="2,2-10" />
  		                <location row="6" col="5" id="2,2-11" />
  		                <location row="6" col="4" id="2,2-12" />
  		                <location row="6" col="3" id="2,2-13" />
  		                <location row="6" col="2" id="2,2-14" />
  		                <location row="5" col="2" id="2,2-15" />
  		                <location row="4" col="2" id="2,2-16" />
  		                <location row="9" col="1" id="2,2-17" />
  		                <location row="9" col="2" id="2,2-18" />
  		                <location row="9" col="3" id="2,2-19" />
  		                <location row="9" col="4" id="2,2-20" />
  		                <location row="9" col="5" id="2,2-21" />
  		                <location row="9" col="6" id="2,2-22" />
  		                <location row="9" col="7" id="2,2-23" />
  		                <location row="9" col="8" id="2,2-24" />
  		                <location row="9" col="9" id="2,2-25" />
  		            </locations>
  		        </entity>
  		    </entities>
  		</room>
  		...
		</rooms>
</the_grid>
