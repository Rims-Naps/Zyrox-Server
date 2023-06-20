package com.zenyte.game.content.grandexchange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class JSONGEItemDefinitions {

    //All of id, name and price are used!
	private int id;
	private String name;
	private int price;

	private Instant time;//Unused entirely

}
