//defaults
import 'package:flutter/material.dart';

//custom
import "../../Api_Services/ApiCall.dart";
import "../../Api_Services/Uri.dart";
import '../../User/current_user.dart';
import '../../Models/Company/position_model.dart';

class UpdatePosition extends StatefulWidget{

	final PositionClass obj;
	UpdatePosition(this.obj);

	@override
	UpdatePositionState createState() => UpdatePositionState();
}

class UpdatePositionState extends State<UpdatePosition>{
	bool autoValidate=false;
	bool isLoading = false;
	bool getNewCompanyData = true;
	List<bool> company = [false,false,false,false];//[C,R,U,D]
	List<bool> companyBranch = [false,false,false,false];
	List<bool> companyExecutive = [false,false,false,false];
	List<bool> client = [false,false,false,false];
	List<bool> product = [false,false,false,false];
	List<bool> location = [false,false,false,false];
	List<bool> enquiry = [false,false,false,false];
	List<bool> ticket = [false,false,false,false];
	List<bool> position = [false,false,false,false];
	TextEditingController positionName = new TextEditingController();
	TextEditingController positionPriority = new TextEditingController();
	GlobalKey<FormState> formKey = new GlobalKey<FormState>();

	List<DropdownMenuItem<int>> companyMenuItems = [];

	int companyValue = -1;

	@override
	void initState(){
		super.initState();
		initialChanges();
	}

	List<bool> changeCheckboxValue(String value){
		List<bool> temp = [false,false,false,false];
		switch(value){
			case "CRUD" : {
				temp = [true,true,true,true];
			}
			break;
			case "RUD" : {
				temp = [false,true,true,true];
			}
			break;
			case "CUD" : {
				temp = [true,false,true,true];
			}
			break;
			case "CRD" : {
				temp = [true,true,false,true];
			}
			break;
			case "CRU" : {
				temp = [true,true,true,false];
			}
			break;
			case "CR" : {
				temp = [true,true,false,false];
			}
			break;
			case "CU" : {
				temp = [true,false,true,false];
			}
			break;
			case "CD" : {
				temp = [true,false,false,true];
			}
			break;
			case "RU" : {
				temp = [false,true,true,false];
			}
			break;
			case "RD" : {
				temp = [false,true,false,true];
			}
			break;
			case "UD" : {
				temp = [false,false,true,true];
			}
			break;
			case "C" : {
				temp = [true,false,false,false];
			}
			break;
			case "R" : {
				temp = [false,true,false,false];
			}
			break;
			case "U" : {
				temp = [false,false,true,false];
			}
			break;
			case "D" : {
				temp = [false,false,false,true];
			}
			break;

		}
		return temp;
	}

	void initialChanges(){
		positionName.text = widget.obj.positionName;
		positionPriority.text = widget.obj.positionPriority.toString();
		companyValue = widget.obj.companyId;
		company = changeCheckboxValue(widget.obj.company);
		companyBranch = changeCheckboxValue(widget.obj.companyBranch);
		companyExecutive = changeCheckboxValue(widget.obj.companyExecutive);
		client = changeCheckboxValue(widget.obj.client);
		product = changeCheckboxValue(widget.obj.product);
		location = changeCheckboxValue(widget.obj.location);
		enquiry = changeCheckboxValue(widget.obj.enquiry);
		ticket = changeCheckboxValue(widget.obj.ticket);
		position = changeCheckboxValue(widget.obj.position);
	}

	bool checkPositionPriority(String value){
		try{
			int a = int.parse(value);
			bool b = a>=2&&a<=25 ? true : false;
			return b;
		}
		catch(e){
			return false;
		}
	}


	String createString(List<bool> values){
		String temp='';
		if(values[0]){
			temp='C';
		}
		if(values[1]){
			temp = temp + 'R';
		}
		if(values[2]){
			temp = temp + 'U';
		}
		if(values[3]){
			temp = temp + 'D';
		}
		return temp;
	}

	updateData() async{
		setState((){
			isLoading = true;
		});
		String companyString = createString(company);
		String companyBranchString = createString(companyBranch);
		String companyExecutiveString = createString(companyExecutive);
		String clientString = createString(client);
		String productString = createString(product);
		String locationString = createString(location);
		String enquiryString = createString(enquiry);
		String ticketString = createString(ticket);
		String positionString = createString(position);
		String lastEditOn = DateTime.now().toString().substring(0,19);

		PositionClass p1 = PositionClass.toPut(
			positionName.text,
			int.parse(positionPriority.text),
			companyValue,	
			companyString,
			companyBranchString,
			companyExecutiveString,
			clientString,
			productString,
			locationString,
			enquiryString,
			ticketString,
			positionString,
			CurrentUser.id,
			lastEditOn
		);
		PositionClass p2 = await updateRecord(p1.toPutMap(),widget.obj.positionId);
		setState((){
			isLoading = false;
		});
		Navigator.pop(context,true);
	}

	updateRecord(dataa, int id) async {
		var response1 = await ApiCall.updateRecord(Uri.GET_POSITION + "/${id}?companyExecutiveId=${CurrentUser.id}", dataa);
	}

	Future<String> getCompanyData()async{
		if(getNewCompanyData){
			getNewCompanyData = false;
			var response1 = await ApiCall.getDataFromApi(Uri.GET_COMPANY);
			companyMenuItems.clear();
			for(int i=0;i<response1.length;i++){
				companyMenuItems.add(
					DropdownMenuItem<int>(
						value : response1[i]['companyId'],
						child : Text(response1[i]['companyName'])
					),
				);
			}
			return 'done';
		}
		else{
			return 'done';
		}
	}

	@override
	Widget build(BuildContext context){
		return Scaffold(
			appBar : AppBar(
				title : Text('Update Position'),
			),
			body : IgnorePointer(
				ignoring : isLoading,
				child : Stack(
					children : <Widget>[
						FutureBuilder(
							future : getCompanyData(),
							builder : (c,s){
								if(s.data == null){
									return Center(child : CircularProgressIndicator());
								}
								else{
									return Form(
										key : formKey,
										autovalidate : autoValidate,
										child : ListView(
											children : <Widget>[
												Padding(
												padding : const EdgeInsets.only(left : 10.0,right : 10.0),
												child : TextFormField(
														controller : positionName,
														decoration : InputDecoration(
															hintText : 'Enter Position Name',
															labelText : 'Position name',	
														),
														validator : (v) => v.isEmpty ? 'Position Name is required .' : null,
													),
												),
												Padding(
												padding : const EdgeInsets.only(left : 10.0,right : 10.0),
												child : TextFormField(
														controller : positionPriority,
														decoration : InputDecoration(
															hintText : 'Enter Position Priority',	
															labelText : 'Position priority',
														),
														validator : (v) => checkPositionPriority(v) ? null : 'Position Priority should be between 1 to 25',
													),
												),
											Padding(
												padding: EdgeInsets.only(left: 10.0, right: 10.0),
												child: DropdownButtonHideUnderline(
													child : DropdownButtonFormField<int>(
														validator : (v) => v == null ? 'Please select company' : null,
														isDense : true,
													decoration : InputDecoration(
														labelText : 'Company',
														hintText : 'Select Company',
													),
													autovalidate : autoValidate,
													items : companyMenuItems,
													onChanged : (v){
														setState((){
															companyValue = v;
														});
														
													},
													value : companyValue == -1 ? null : companyValue,
													),
												),
												
						                    ),		
												ExpansionTile(
											        title : Text('Company'),
											        children : <Widget>[
											          Row(
											            children : <Widget>[
											            
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : company[0],
											                onChanged : (bool value){
											                	setState((){company[0] = !company[0];});
											                }
											              ),
											              Text('Create'),
											              SizedBox(width : 50.0),
											              Checkbox(
											              	value : company[2],
											                onChanged : (bool value){
											                	setState((){company[2] = !company[2];});
											                }
											              ),
											              Text('Update'),
											              
											            ]
											          ),
											          Row(
											            children : <Widget>[
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : company[1],
											                onChanged : (bool value){
											                	//setState((){company[1] = !company[1];});
											                }
											              ),
											              Text('Read'),
											              SizedBox(width : 58.0),
											              Checkbox(
											                value : company[3],
											                onChanged : (bool value){
											                	setState((){company[3] = !company[3];});
											                }
											              ),
											              Text('Delete'),
											              
											            ]
											          ),
											        ]
											    ),
											    ExpansionTile(
											        title : Text('Company Branch'),
											        children : <Widget>[
											          Row(
											            children : <Widget>[
											            
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : companyBranch[0],
											                onChanged : (bool value){
											                	setState((){companyBranch[0] = !companyBranch[0];});
											                }
											              ),
											              Text('Create'),
											              SizedBox(width : 50.0),
											              Checkbox(
											              	value : companyBranch[2],
											                onChanged : (bool value){
											                	setState((){companyBranch[2] = !companyBranch[2];});
											                }
											              ),
											              Text('Update'),
											              
											            ]
											          ),
											          Row(
											            children : <Widget>[
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : companyBranch[1],
											                onChanged : (bool value){
											                	//setState((){companyBranch[1] = !companyBranch[1];});
											                }
											              ),
											              Text('Read'),
											              SizedBox(width : 58.0),
											              Checkbox(
											                value : companyBranch[3],
											                onChanged : (bool value){
											                	setState((){companyBranch[3] = !companyBranch[3];});
											                }
											              ),
											              Text('Delete'),
											              
											            ]
											          ),
											        ]
											    ),
											    ExpansionTile(
											        title : Text('Company Executive'),
											        children : <Widget>[
											          Row(
											            children : <Widget>[
											            
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : companyExecutive[0],
											                onChanged : (bool value){
											                	setState((){companyExecutive[0] = !companyExecutive[0];});
											                }
											              ),
											              Text('Create'),
											              SizedBox(width : 50.0),
											              Checkbox(
											              	value : companyExecutive[2],
											                onChanged : (bool value){
											                	setState((){companyExecutive[2] = !companyExecutive[2];});
											                }
											              ),
											              Text('Update'),
											              
											            ]
											          ),
											          Row(
											            children : <Widget>[
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : companyExecutive[1],
											                onChanged : (bool value){
											                	//setState((){companyExecutive[1] = !companyExecutive[1];});
											                }
											              ),
											              Text('Read'),
											              SizedBox(width : 58.0),
											              Checkbox(
											                value : companyExecutive[3],
											                onChanged : (bool value){
											                	setState((){companyExecutive[3] = !companyExecutive[3];});
											                }
											              ),
											              Text('Delete'),
											              
											            ]
											          ),
											        ]
											    ),
											    ExpansionTile(
											        title : Text('Client'),
											        children : <Widget>[
											          Row(
											            children : <Widget>[
											            
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : client[0],
											                onChanged : (bool value){
											                	setState((){client[0] = !client[0];});
											                }
											              ),
											              Text('Create'),
											              SizedBox(width : 50.0),
											              Checkbox(
											              	value : client[2],
											                onChanged : (bool value){
											                	setState((){client[2] = !client[2];});
											                }
											              ),
											              Text('Update'),
											              
											            ]
											          ),
											          Row(
											            children : <Widget>[
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : client[1],
											                onChanged : (bool value){
											                	//setState((){client[1] = !client[1];});
											                }
											              ),
											              Text('Read'),
											              SizedBox(width : 58.0),
											              Checkbox(
											                value : client[3],
											                onChanged : (bool value){
											                	setState((){client[3] = !client[3];});
											                }
											              ),
											              Text('Delete'),
											              
											            ]
											          ),
											        ]
											    ),
											    ExpansionTile(
											        title : Text('Product'),
											        children : <Widget>[
											          Row(
											            children : <Widget>[
											            
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : product[0],
											                onChanged : (bool value){
											                	setState((){product[0] = !product[0];});
											                }
											              ),
											              Text('Create'),
											              SizedBox(width : 50.0),
											              Checkbox(
											              	value : product[2],
											                onChanged : (bool value){
											                	setState((){product[2] = !product[2];});
											                }
											              ),
											              Text('Update'),
											              
											            ]
											          ),
											          Row(
											            children : <Widget>[
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : product[1],
											                onChanged : (bool value){
											                	//setState((){product[1] = !product[1];});
											                }
											              ),
											              Text('Read'),
											              SizedBox(width : 58.0),
											              Checkbox(
											                value : product[3],
											                onChanged : (bool value){
											                	setState((){product[3] = !product[3];});
											                }
											              ),
											              Text('Delete'),
											              
											            ]
											          ),
											        ]
											    ),
											    ExpansionTile(
											        title : Text('Location'),
											        children : <Widget>[
											          Row(
											            children : <Widget>[
											            
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : location[0],
											                onChanged : (bool value){
											                	setState((){location[0] = !location[0];});
											                }
											              ),
											              Text('Create'),
											              SizedBox(width : 50.0),
											              Checkbox(
											              	value : location[2],
											                onChanged : (bool value){
											                	setState((){location[2] = !location[2];});
											                }
											              ),
											              Text('Update'),
											              
											            ]
											          ),
											          Row(
											            children : <Widget>[
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : location[1],
											                onChanged : (bool value){
											                	//setState((){location[1] = !location[1];});
											                }
											              ),
											              Text('Read'),
											              SizedBox(width : 58.0),
											              Checkbox(
											                value : location[3],
											                onChanged : (bool value){
											                	setState((){location[3] = !location[3];});
											                }
											              ),
											              Text('Delete'),
											              
											            ]
											          ),
											        ]
											    ),
											    ExpansionTile(
											        title : Text('Enquiry'),
											        children : <Widget>[
											          Row(
											            children : <Widget>[
											            
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : enquiry[0],
											                onChanged : (bool value){
											                	setState((){enquiry[0] = !enquiry[0];});
											                }
											              ),
											              Text('Create'),
											              SizedBox(width : 50.0),
											              Checkbox(
											              	value : enquiry[2],
											                onChanged : (bool value){
											                	setState((){enquiry[2] = !enquiry[2];});
											                }
											              ),
											              Text('Update'),
											              
											            ]
											          ),
											          Row(
											            children : <Widget>[
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : enquiry[1],
											                onChanged : (bool value){
											                	//setState((){enquiry[1] = !enquiry[1];});
											                }
											              ),
											              Text('Read'),
											              SizedBox(width : 58.0),
											              Checkbox(
											                value : enquiry[3],
											                onChanged : (bool value){
											                	setState((){enquiry[3] = !enquiry[3];});
											                }
											              ),
											              Text('Delete'),
											              
											            ]
											          ),
											        ]
											    ),
											    ExpansionTile(
											        title : Text('Ticket'),
											        children : <Widget>[
											          Row(
											            children : <Widget>[
											            
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : ticket[0],
											                onChanged : (bool value){
											                	setState((){ticket[0] = !ticket[0];});
											                }
											              ),
											              Text('Create'),
											              SizedBox(width : 50.0),
											              Checkbox(
											              	value : ticket[2],
											                onChanged : (bool value){
											                	setState((){ticket[2] = !ticket[2];});
											                }
											              ),
											              Text('Update'),
											              
											            ]
											          ),
											          Row(
											            children : <Widget>[
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : ticket[1],
											                onChanged : (bool value){
											                	//setState((){ticket[1] = !ticket[1];});
											                }
											              ),
											              Text('Read'),
											              SizedBox(width : 58.0),
											              Checkbox(
											                value : ticket[3],
											                onChanged : (bool value){
											                	setState((){ticket[3] = !ticket[3];});
											                }
											              ),
											              Text('Delete'),
											              
											            ]
											          ),
											        ]
											    ),
											    ExpansionTile(
											        title : Text('Position'),
											        children : <Widget>[
											          Row(
											            children : <Widget>[
											            
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : position[0],
											                onChanged : (bool value){
											                	setState((){position[0] = !position[0];});
											                }
											              ),
											              Text('Create'),
											              SizedBox(width : 50.0),
											              Checkbox(
											              	value : position[2],
											                onChanged : (bool value){
											                	setState((){position[2] = !position[2];});
											                }
											              ),
											              Text('Update'),
											              
											            ]
											          ),
											          Row(
											            children : <Widget>[
											              SizedBox(width : 50.0),
											              Checkbox(
											                value : position[1],
											                onChanged : (bool value){
											                	//setState((){position[1] = !position[1];});
											                }
											              ),
											              Text('Read'),
											              SizedBox(width : 58.0),
											              Checkbox(
											                value : position[3],
											                onChanged : (bool value){
											                	setState((){position[3] = !position[3];});
											                }
											              ),
											              Text('Delete'),
											              
											            ]
											          ),
											        ]
											    ),
											    Container(
															padding: EdgeInsets.only(left: 60.0, top: 20.0, right: 60.0),
															height: 65.0,
															child: FlatButton(
																textColor: Colors.black,
																color: Colors.white,
																shape: RoundedRectangleBorder(
																	borderRadius: new BorderRadius.circular(4.0),
																	side: BorderSide(color: Theme.of(context).accentColor),
																),
																padding: const EdgeInsets.all(8.0),
																child: Text(
																	"Update",
																	style: TextStyle(
																	fontWeight: FontWeight.bold,
																	fontSize: 20.0,
																	),
																),
																onPressed: (){
																	if(formKey.currentState.validate()){
																		updateData();
																	}
																	else
																	{
																		setState((){
																			autoValidate = true;
																		});
																	}
																}
															),
														),
											    Container(
															height: 20.0,
														),

											]
										),
									);
								}
							}
						),
						isLoading ? Center(child : CircularProgressIndicator()) : Container(),
					]
				),
			),
		);
	}
}