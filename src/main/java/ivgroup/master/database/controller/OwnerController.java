package ivgroup.master.database.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ivgroup.master.database.Constants;
import ivgroup.master.database.bl.OwnerBusinessLogic;
import ivgroup.master.database.dto.owner.OwnerInsert;
import ivgroup.master.database.dto.owner.OwnerLoginCredentials;
import ivgroup.master.database.dto.owner.OwnerSelect;
import ivgroup.master.database.dto.owner.OwnerUpdate;

@RestController
@RequestMapping("/owner")
@CrossOrigin(origins = Constants.URL_TO_REQUEST)
public class OwnerController {

	@Autowired
	OwnerBusinessLogic obl;
	
	@GetMapping(path="/{ownerId}",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<OwnerSelect> selectOwnerById(@PathVariable @NotNull Long ownerId)
	{
		return obl.selectOwnerById(ownerId);
	}
	
	@PostMapping(path="/login",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Long> loginOwner(@RequestBody @Valid OwnerLoginCredentials olc)
	{
		return obl.loginOwner(olc);
	}
	
	@PostMapping(
				consumes = { 	MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, 
				produces = {	MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Void> addOwner(@RequestBody @Valid OwnerInsert oi)
	{
		return obl.addOwner(oi);
	}

	@DeleteMapping(path="/{ownerId}")
	public ResponseEntity<Void> deleteOwner(@PathVariable @NotNull Long ownerId)
	{
		return obl.deleteOwner(ownerId);
	}

	@PutMapping(path = "/{ownerId}",	consumes = { 	MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, 
										produces = {	MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Void> updateOwnerFields(@PathVariable @NotNull Long ownerId,@RequestBody @Valid OwnerUpdate ou)
	{
		return obl.updateOwnerFields(ownerId, ou);
	}

	@PutMapping(path = "/secretkey/{ownerId}",	consumes = { 	MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, 
			produces = {	MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Void> updateOwnerOwnerSecretKey(@PathVariable @NotNull Long ownerId) 
	{
		return obl.updateOwnerOwnerSecretKey(ownerId);
	}
	
	@GetMapping(path="/email/{emailId}",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Integer> sendOTPVerificationEmail(@PathVariable @NotNull String emailId)
	{
		return obl.sendOTPVerificationEmail(emailId);
	}
	
}
