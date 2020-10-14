## interactive_story_1
## happy path
* greet
    - utter_answer_greet
* request_number
    - number_form
    - form{"name": "number_form"}
    - form{"name": null}
* thanks
    - utter_noworries
    
## interactive_story_2
## happy path
* greet
    - utter_answer_greet
* request_number
    - number_form
    - form{"name": "number_form"}
    - slot{"requested_slot": "business"}
* form: inform{"business": "开房记录"}
    - slot{"business": "开房记录"}
    - form: restaurant_form
    - slot{"business": "开房记录"}
* thanks
    - utter_noworries

## interactive_story_3
# happy path：item+number+business+business+business
* request_number{"type": "身份证号码"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "身份证号码"}
    - slot{"requested_slot": "number"}
* form: request_number{"number": "431124199720139720"}
    - form: number_form
    - slot{"number": "431124199720139720"}
    - slot{"type": "身份证号码"}
    - form{"name": null}
    - slot{"requested_slot": null}
* inform_business{"business": "违章记录"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "身份证号码"}
    - slot{"number": "431124199720139720"}
    - slot{"business": "违章记录"}
    - slot{"business": null}
    - form{"name": null}
    - slot{"requested_slot": null}
* inform_business{"business": "开房信息"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "身份证号码"}
    - slot{"number": "431124199720139720"}
    - slot{"business": "开房信息"}
    - slot{"business": null}
    - form{"name": null}
    - slot{"requested_slot": null}
* inform_business{"business": "出行轨迹"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "身份证号码"}
    - slot{"number": "431124199720139720"}
    - slot{"business": "出行轨迹"}
    - slot{"business": null}
    - form{"name": null}
    - slot{"requested_slot": null}
* affirm
    - utter_answer_affirm

## interactive_story_4
# happy path：number+business+business+business
* request_number{"number": "18902346721"}
    - number_form
    - form{"name": "number_form"}
    - slot{"number": "18902346721"}
    - slot{"type": "电话号码"}
    - form{"name": null}
    - slot{"requested_slot": null}
* inform_business{"business": "违章记录"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "电话号码"}
    - slot{"number": "18902346721"}
    - slot{"business": "违章记录"}
    - slot{"business": null}
    - form{"name": null}
    - slot{"requested_slot": null}
* inform_business{"business": "犯罪记录"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "电话号码"}
    - slot{"number": "18902346721"}
    - slot{"business": "犯罪记录"}
    - slot{"business": null}
    - form{"name": null}
    - slot{"requested_slot": null}
* inform_business{"business": "开房信息"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "电话号码"}
    - slot{"number": "18902346721"}
    - slot{"business": "开房信息"}
    - slot{"business": null}
    - form{"name": null}
    - slot{"requested_slot": null}
* affirm
    - utter_answer_affirm    
    
## interactive_story_5
# unhappy：chit+answer
* greet
    - utter_answer_greet
* request_number{"type": "身份证号码"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "身份证号码"}
    - slot{"number": null}
    - slot{"business": null}
    - slot{"requested_slot": "number"}
* chitchat{"date_time": "今天"}
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* form: request_number{"number": "440123199087233467"}
    - form: number_form
    - slot{"number": "440123199087233467"}
    - slot{"type": "身份证号码"}
    - form{"name": null}
    - slot{"requested_slot": null}
* thanks
    - utter_noworries

## interactive_story_6
# very unhappy：chit + chit + answer
* greet
    - utter_answer_greet
* request_number{"type": "电话号码"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "电话号码"}
    - slot{"number": null}
    - slot{"business": null}
    - slot{"requested_slot": "number"}
* chitchat
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* chitchat
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* form: request_number{"number": "18902346721"}
    - form: number_form
    - slot{"number": "18902346721"}
    - slot{"type": "电话号码"}
    - form{"name": null}
    - slot{"requested_slot": null}
* thanks
    - utter_noworries
    
## interactive_story_7
# very very unhappy：chitchat+chitchat+chitchat+answer
* greet
    - utter_answer_greet
* request_number{"type": "电话号码"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "电话号码"}
    - slot{"number": null}
    - slot{"business": null}
    - slot{"requested_slot": "number"}
* chitchat
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* chitchat
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* chitchat
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* form: request_number{"number": "18902346721"}
    - form: number_form
    - slot{"number": "18902346721"}
    - slot{"type": "电话号码"}
    - form{"name": null}
    - slot{"requested_slot": null}
* thanks
    - utter_noworries

## interactive_story_8
## unhappy path：stop and really stop path
* greet
    - utter_answer_greet
* request_number{"type": "身份证号码"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "身份证号码"}
    - slot{"number": null}
    - slot{"business": null}
    - slot{"requested_slot": "number"}
* stop
    - utter_ask_continue
* deny
    - action_deactivate_form
    - form{"name": null}
    - slot{"requested_slot": null}
* thanks
    - utter_noworries
    
## interactive_story_9
## unhappy path：stop but continue path
* greet
    - utter_answer_greet
* request_number{"type": "电话号码"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "电话号码"}
    - slot{"number": null}
    - slot{"business": null}
    - slot{"requested_slot": "number"}
* stop
    - utter_ask_continue
* affirm
    - number_form
    - slot{"requested_slot": "number"}
* form: request_number{"number": "18902346721"}
    - form: number_form
    - slot{"number": "18902346721"}
    - slot{"type": "电话号码"}
    - form{"name": null}
    - slot{"requested_slot": null}
* thanks
    - utter_noworries
    
## interactive_story_10
# unhappy path：chitchat stop but continue path
* greet
    - utter_answer_greet
* request_number{"type": "身份证号码"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "身份证号码"}
    - slot{"number": null}
    - slot{"business": null}
    - slot{"requested_slot": "number"}
* chitchat
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* stop
    - utter_ask_continue
* affirm
    - number_form
    - slot{"requested_slot": "number"}
* form: request_number{"number": "440123199087233467"}
    - form: number_form
    - slot{"number": "440123199087233467"}
    - slot{"type": "身份证号码"}
    - form{"name": null}
    - slot{"requested_slot": null}
* thanks
    - utter_noworries
    
## interactive_story_11
## unhappy path: stop but continue and chitchat path
* greet
    - utter_answer_greet
* request_number{"type": "电话号码"}
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "电话号码"}
    - slot{"number": null}
    - slot{"business": null}
    - slot{"requested_slot": "number"}
* stop
    - utter_ask_continue
* affirm
    - number_form
    - slot{"requested_slot": "number"}
* chitchat
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* form: request_number{"number": "18902346721"}
    - form: number_form
    - slot{"number": "18902346721"}
    - slot{"type": "电话号码"}
    - form{"name": null}
    - slot{"requested_slot": null}
* thanks
    - utter_noworries
    
## interactive_story_12
## unhappy path: chitchat stop but continue and chitchat path
* greet
    - utter_answer_greet
* request_number
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "电话号码"}
    - slot{"number": null}
    - slot{"business": null}
    - slot{"requested_slot": "number"}
* chitchat
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* stop
    - utter_ask_continue
* affirm
    - number_form
    - slot{"requested_slot": "number"}
* chitchat
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* form: request_number{"number": "18902346721"}
    - form: number_form
    - slot{"number": "18902346721"}
    - slot{"type": "电话号码"}
    - form{"name": null}
    - slot{"requested_slot": null}
* thanks
    - utter_noworries    
    
## interactive_story_13
## unhappy path:  chitchat, stop and really stop path
* greet
    - utter_answer_greet
* request_number
    - number_form
    - form{"name": "number_form"}
    - slot{"type": "电话号码"}
    - slot{"number": null}
    - slot{"business": null}
    - slot{"requested_slot": "number"}
* chitchat
    - utter_chitchat
    - number_form
    - slot{"requested_slot": "number"}
* stop
    - utter_ask_continue
* deny
    - action_deactivate_form
    - form{"name": null}
    - slot{"requested_slot": null}
* thanks
    - utter_noworries