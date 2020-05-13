package ivgroup.master.database.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ivgroup.master.database.Constants;
import ivgroup.master.database.bl.CompanyExecutiveBusinessLogic;
import ivgroup.master.database.dto.companyExecutive.CompanyExecutiveInsert;
import ivgroup.master.database.dto.companyExecutive.CompanyExecutiveLogin;
import ivgroup.master.database.dto.companyExecutive.CompanyExecutiveSelect;
import ivgroup.master.database.dto.companyExecutive.CompanyExecutiveUpdate;

@RestController
@RequestMapping("/companyExecutive")
@CrossOrigin(origins = Constants.URL_TO_REQUEST)
public class CompanyExecutiveController {

	@Autowired
	CompanyExecutiveBusinessLogic cebl;
	
	@PostMapping(consumes = { 	MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, 
			produces = {	MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Void> addCompanyExecutive(@Valid @RequestBody CompanyExecutiveInsert ci)
	{
		return cebl.addCompanyExecutive(ci);
	}
	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<List<CompanyExecutiveSelect>> selectCompanyExecutive()
	{
		return cebl.selectCompanyExecutive();
	}
	@GetMapping(path="/{companyExecutiveID}",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<CompanyExecutiveSelect> selectCompanyByCompanyExecutiveId(@PathVariable @NotNull Long companyExecutiveID)
	{
		return cebl.selectCompanyByCompanyExecutiveId(companyExecutiveID);
	}	
	@GetMapping(path="/company/{companyId}",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<List<CompanyExecutiveSelect>> selectCompanyExecutiveByCompanyId(@PathVariable @NotNull Long companyId)
	{
		return cebl.selectCompanyExecutiveByCompanyId(companyId);
	}
	@GetMapping(path="/owner/{ownerId}",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<List<CompanyExecutiveSelect>> selectCompanyExecutiveByOwnerId(@PathVariable @NotNull Long ownerId)
	{
		return cebl.selectCompanyExecutiveByOwnerId(ownerId);
	}
	@GetMapping(path="/branch/{companyBranchId}",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<List<CompanyExecutiveSelect>> selectCompanyExecutiveByCompanyBranchId(@PathVariable @NotNull Long companyBranchId)
	{
		return cebl.selectCompanyExecutiveByCompanyBranchId(companyBranchId);
	}
	@GetMapping(path="/subPosition/{companyExecutiveId}",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<List<CompanyExecutiveSelect>> selectCompanyExecutiveOfSubPosition(@PathVariable @NotNull Long companyExecutiveId)
	{
		return cebl.selectCompanyExecutiveOfSubPosition(companyExecutiveId);
	}
	
	@DeleteMapping
	public ResponseEntity<Void> deleteCompanyExecutive(@RequestParam(name = "companyExecutiveId") Long companyExecutiveId,@RequestParam(name = "companyId") Long companyId)
	{
		return cebl.deleteCompanyExecutive(companyExecutiveId, companyId);
	}
	
	@PutMapping(path = "/{companyExecutiveId}",	consumes = { 	MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, 
			produces = {	MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Void> updateFields(@PathVariable @NotNull Long companyExecutiveId,@Valid @RequestBody CompanyExecutiveUpdate cu) 
	{
		return cebl.updateCompanyExecutiveFields(companyExecutiveId, cu);
	}
	
	@GetMapping(path="/login",produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<CompanyExecutiveLogin> loginCompanyExecutive(@RequestParam("loginId") @NotNull String loginId,@RequestParam("password") @NotNull String password)
	{
		return cebl.loginCompanyExecutive(loginId, password);
	}

}