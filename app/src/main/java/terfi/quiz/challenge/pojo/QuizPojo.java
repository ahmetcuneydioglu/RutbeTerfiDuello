package terfi.quiz.challenge.pojo;

public class QuizPojo {
	

	String _question;
    String _option1;
    String _option2;
    String _option3;
    String _option4;
    String _answer;
    String category_name;
    
	
	
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	
	
	public QuizPojo(String _question, String _option1, String _option2,
			String _option3, String _option4,  String _answer, 
			 String category_name) {
		super();
		this._question = _question;
		this._option1 = _option1;
		this._option2 = _option2;
		this._option3 = _option3;
		this._option4 = _option4;
		this._answer = _answer;
		this.category_name = category_name;
	}


    //private variables
    int _id;
    public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String get_question() {
		return _question;
	}
	public void set_question(String _question) {
		this._question = _question;
	}
	public String get_option1() {
		return _option1;
	}
	public void set_option1(String _option1) {
		this._option1 = _option1;
	}
	public String get_option2() {
		return _option2;
	}
	public void set_option2(String _option2) {
		this._option2 = _option2;
	}
	public String get_option3() {
		return _option3;
	}
	public void set_option3(String _option3) {
		this._option3 = _option3;
	}
	public String get_option4() {
		return _option4;
	}
	public void set_option4(String _option4) {
		this._option4 = _option4;
	}
	public String get_answer() {
		return _answer;
	}
	public void set_answer(String _answer) {
		this._answer = _answer;
	}



    
     
    // Empty constructor
    public QuizPojo(){
         
    }
}