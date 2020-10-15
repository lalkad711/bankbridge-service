Feature: Bank details from v1 and v2 Bank Bridge Service
    I can get all bank details from v1
    I can get all bank details from v1

    Scenario: Get bank details from v1
        When The client does a get on v1 bank bridge service
        Then The client gets back status code of 200 from v1
        And Json reponse for v1 contains following bank names:
        |id|name|
        |"5678"|"Credit Sweets"|
        |"9870"|"Banco de espiritu santo"|
        |"1234"|"Royal Bank of Boredom"|
        
    Scenario: Get bank details from v2
        When The client does a get on v2 bank bridge service
        Then The client gets back status code of 200 from v2
        And Json reponse for v2 contains following bank names:
        |id|name|
        ||"Credit Sweets"|
        |"9870"|"Banco de espiritu santo"|
        |"1234"|"Royal Bank of Boredom"|